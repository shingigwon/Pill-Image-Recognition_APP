const express = require('express');
const bodyParser = require('body-parser'); //json 파싱용

const mysql = require('mysql');

const multer = require('multer');
const path = require('path'); // path 모듈을 추가
const fs = require('fs'); // fs 모듈을 추가

const { Readable } = require('stream');
const util = require('util');

const { execFile } = require('child_process');
const exec = util.promisify(require('child_process').exec);

const app = express();
const port = 3004;

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));

//mysql연결
const connection = mysql.createConnection({
  host: 'localhost',
  user: 'root',
  password: '1234',
  database: 'android_project',
});
connection.connect();

// 이미지가 저장된 디렉토리 경로
const uploadDir = path.join(__dirname, 'uploads');

// 저장 함수
const storage = multer.diskStorage({
  destination: (req, file, cb) => {
    const userId = req.params.id; // URL에서 사용자 ID를 추출합니다.
    const uploadDirUser = path.join(uploadDir, userId);
    if (!fs.existsSync(uploadDirUser)) {
      fs.mkdirSync(uploadDirUser, { recursive: true });
    }
    cb(null, uploadDirUser);
  },
  filename: (req, file, cb) => {
    const formattedDate = getFormattedDate(); // 적절한 날짜 형식 생성 함수
    cb(null, formattedDate + '.png');
  },
});

const upload = multer({ storage: storage });

//client -> server 이미지 전송
app.post('/upload/:id', upload.single('image'), (req, res) => {
  if (!req.file) {
    return res.json({ success: false, filename: '파일 전송 에러' });
  }

  const inputFilePath = req.file.path;

  if (!isImageFile(inputFilePath)) {
    fs.unlink(inputFilePath, (err) => {
      if (err) console.error('파일 삭제 오류:', err);
    });
    return res.json({
      success: false,
      filename: '유효한 이미지 파일이 아닙니다.',
    });
  }
  execFile(
    'python',
    ['Python/Rembg_Image.py', inputFilePath],
    (error, stdout, stderr) => {
      if (error) {
        console.error('Error executing Python script:', error);
        return res.json({ success: false, filename: 'python rembg error' });
      }
      res.json({ success: true, filename: path.basename(inputFilePath) });
    }
  );
});

// server-> python -> mysql -> client 반환
// #region
app.get('/download/:id/:filename', async (req, res) => {
  const id = req.params.id;
  const filename = req.params.filename;
  const imagePath = path.join(uploadDir, id, filename);
  try {
    if (!fs.existsSync(imagePath)) {
      throw new Error('이미지가 존재하지 않습니다.');
    }

    // OCR 및 모델 예측 비동기 처리
    const ocr_txt = await OCR_Detect_Text(imagePath);
    // console.log(ocr_txt);

    const [shape_Arr, color_Arr] = await Predict_Model(imagePath);
    // console.log(shape_Arr);
    // console.log(color_Arr);

    // MySQL에서 이미지 가져오기
    getImageFromMySQL(shape_Arr, color_Arr, ocr_txt, (error, imageData) => {
      if (error) {
        console.error('MySQL에서 이미지를 가져오는 중 오류 발생:', error);
        return res
          .status(500)
          .send('이미지를 가져오는 중 오류가 발생했습니다.');
      }
      if (!imageData) {
        console.log('해당 이미지를 DB에서 찾을 수 없습니다.');
        return res.status(404).send('해당 이미지를 DB에서 찾을 수 없습니다.');
      }

      const {
        imageStream,
        item_name,
        company,
        classification_name,
        EE,
        UD,
        NB,
      } = imageData;

      // 이미지 스트림을 버퍼로 읽기
      let chunks = [];
      imageStream.on('data', (chunk) => {
        chunks.push(chunk);
      });
      imageStream.on('end', () => {
        // 모든 데이터를 읽은 후 버퍼로 합치기
        const buffer = Buffer.concat(chunks);
        // 버퍼를 Base64로 인코딩하여 문자열로 변환
        const image64 = buffer.toString('base64');
        // JSON 객체 생성
        const responseData = {
          image64: image64, // Base64로 인코딩된 이미지 데이터
          filename: item_name,
          company: company,
          classification_name: classification_name,
          EE: EE,
          UD: UD,
          NB: NB,
        };
        // JSON 객체 응답
        res.json(responseData);
      });
    });
  } catch (error) {
    console.error('에러 발생:', error.message);
    res.status(500).send('서버에서 에러가 발생했습니다.');
  }
});

async function OCR_Detect_Text(imagePath) {
  try {
    const { stdout } = await exec(
      `python Python/OCR/OCR_Image.py ${imagePath}`
    );
    const ocr_txt = JSON.parse(stdout)[0];
    return ocr_txt;
  } catch (error) {
    console.error(`OCR 실행 중 오류 발생: ${error.message}`);
    throw new Error('OCR 실행 중 오류 발생');
  }
}

async function Predict_Model(imageFile) {
  try {
    const { stdout } = await exec(
      `python Python/AI/Predict_Model.py ${imageFile}`
    );
    const resultArray = JSON.parse(stdout);
    const shape_Arr = [];
    const color_Arr = [];
    resultArray.forEach((item) => {
      if (item.endsWith('형') || item === '기타') {
        shape_Arr.push(item);
      } else {
        color_Arr.push(item);
      }
    });

    if (shape_Arr.includes('원형') && !shape_Arr.includes('타원형')) {
      shape_Arr.push('타원형');
    } else if (!shape_Arr.includes('원형') && shape_Arr.includes('타원형')) {
      shape_Arr.push('원형');
    }

    return [shape_Arr, color_Arr];
  } catch (error) {
    console.error(`모델 실행 중 오류 발생: ${error.message}`);
    throw new Error('모델 실행 중 오류 발생');
  }
}

function getImageFromMySQL(shape, color, ocr_txt, callback) {
  const shapeList = shape.map((s) => `'${s}'`).join(', ');
  const colorList = color.map((c) => `'${c}'`).join(', ');

  const query = `SELECT Image, Item_Name, Company, Classification_Name, EE, UD, NB FROM Pill_Info WHERE (Shape IN (${shapeList}) AND Front_Color IN (${colorList})) AND ((Front_Mark REGEXP '${ocr_txt}') AND (Back_Mark REGEXP 'LT'));`;
  // const query = `SELECT Image, Item_Name, Company, Classification_Name, EE, UD, NB FROM Pill_Info WHERE (Shape IN (${shapeList}) AND Front_Color IN (${colorList})) AND CONCAT(Front_Mark, Back_Mark) REGEXP '(${ocr_txt_1}.*${ocr_txt_2}|${ocr_txt_2}.*${ocr_txt_1})'`;

  connection.query(query, (error, results, fields) => {
    if (error) {
      console.error('MySQL 쿼리 실행 중 오류 발생:', error);
      return callback(error, null);
    }
    if (results.length > 0) {
      const imageBuffer = results[0].Image;
      const item_name = results[0].Item_Name;
      const company = results[0].Company;
      const classification_name = results[0].Classification_Name;
      const EE = results[0].EE;
      const UD = results[0].UD;
      const NB = results[0].NB;

      const imageStream = Readable.from(imageBuffer);

      callback(null, {
        imageStream,
        item_name,
        company,
        classification_name,
        EE,
        UD,
        NB,
      });
    } else {
      console.log('해당 OCR 텍스트에 대한 이미지를 찾을 수 없습니다.');
      callback(null, null);
    }
  });
}
//#endregion

//#region  DB
//회원가입
app.post('/join', function (req, res) {
  var name = req.body.name;
  var id = req.body.id;
  var pw = req.body.pw;

  var sql = 'insert into user values(?, ?, ?)';
  var params = [id, pw, name];

  connection.query(sql, params, function (err, result) {
    var success = false;

    if (err) {
      console.log(err);
    } else {
      if (result.length === 0) {
        success = false;
      } else {
        success = true;
      }
    }
    res.json({
      success: success,
    });
  });
});

//로그인
app.post('/login', function (req, res) {
  var id = req.body.id;
  var pw = req.body.pw;

  var sql = 'select name from user where id = ? and pw =?';
  var params = [id, pw];

  connection.query(sql, params, function (err, result) {
    var success = false;
    var name = '에러가 발생했습니다';

    if (err) {
      console.log(err);
    } else {
      if (result.length === 0) {
        success = false;
        name = '입력값을 다시 확인해주세요.';
      } else {
        success = true;
        name = result[0].name;
      }
    }
    res.json({
      success: success,
      name: name,
    });
  });
});

//중복체크
app.post('/checkid', function (req, res) {
  var id = req.body.id;

  var sql = 'select id from user where id = ?';

  connection.query(sql, id, function (err, result) {
    var success = false;

    if (err) {
      console.log(err);
    } else {
      if (result.length === 0) {
        success = false;
      } else {
        success = true;
      }
    }
    res.json({
      success: success,
    });
  });
});

//이름 검색 ***
// app.post('/searchName', function (req, res) {
//   var name = req.body.name;

//   var sql =
//     'SELECT image, item_name, company, Classification_Name, EE, UD, NB FROM pill_info WHERE Item_Name LIKE ?';

//   var searchQuery = '%' + name + '%';

//   connection.query(sql, searchQuery, function (err, result) {
//     var success = false;
//     var data = [];

//     if (err) {
//       console.log(err);
//     } else {
//       if (result.length === 0) {
//         success = false;
//       } else {
//         success = true;
//         result.forEach(function (row) {
//           // 각 행의 데이터를 객체로 만들어 배열에 추가합니다.
//           var item = {
//             image: row.image,
//             item_name: row.item_name,
//             company: row.company,
//             Classification_Name: row.Classification_Name,
//             EE: row.EE,
//             UD: row.UD,
//             NB: row.NB,
//           };
//           data.push(item);
//         });
//       }
//     }
//     res.json({ success: success, DataList: data });
//   });
// });
//#endregion

//#region JS_Code / 현재 시간 포맷, 이미지 파일 확인
function getFormattedDate() {
  const now = new Date();
  const year = now.getFullYear();
  const month = padZero(now.getMonth() + 1);
  const date = padZero(now.getDate());
  const hours = padZero(now.getHours());
  const minutes = padZero(now.getMinutes());
  const seconds = padZero(now.getSeconds());

  return `${year}${month}${date}_${hours}${minutes}${seconds}`;
}

function padZero(num) {
  return num.toString().padStart(2, '0');
}

function isImageFile(filePath) {
  const ext = path.extname(filePath).toLowerCase();
  const imageExts = ['.jpg', '.jpeg', '.png', '.bmp'];
  return imageExts.includes(ext);
}

//#endregion

app.listen(port, () => {
  console.log(`http://localhost:${port}.`);
});
