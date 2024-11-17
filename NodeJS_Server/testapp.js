const express = require('express');
const bodyParser = require('body-parser'); //json 파싱용

const mysql = require('mysql');

const multer = require('multer');
const path = require('path'); // path 모듈을 추가
const fs = require('fs'); // fs 모듈을 추가

const { execFile, spawn } = require('child_process');
const { Readable } = require('stream');

const app = express();
const port = 3004;

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));

const connection = mysql.createConnection({
  host: 'localhost',
  user: 'root',
  password: '1234',
  database: 'android_project',
});
connection.connect();

// 이미지가 저장된 디렉토리 경로
const uploadDir = path.join(__dirname, 'uploads');

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

// app.post('/upload/:id', upload.single('image'), (req, res) => {
//   var success = false;
//   // var destination = '';
//   var filename = '';

//   if (!req.file) {
//     success = false;
//     // destination = 'file 전송 error';
//     filename = 'file 전송 error';
//   }

//   success = true;
//   // destination = req.file.destination;
//   filename = req.file.filename;

//   res.json({
//     success: success,
//     // destination: destination,
//     filename: filename,
//   });
// });

app.post('/upload/:id', upload.single('image'), (req, res) => {
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

  if (!req.file) {
    return res.json({
      success: false,
      // destination = 'file 전송 error';
      filename: 'file 전송 error',
    });
  }
  execFile(
    'python',
    ['Python/Rembg_Image.py', inputFilePath],
    (error, stdout, stderr) => {
      if (error) {
        console.error('Error executing Python script:', error);
        res.status(404).send(error);
      }
      res.json({ success: true, filename: path.basename(inputFilePath) });
    }
  );
});

// 이미지를 GET 요청
app.get('/download/:id/:filename', (req, res) => {
  const id = req.params.id;
  const filename = req.params.filename;
  const imagePath = path.join(uploadDir, id, filename);

  // let ocr_arr = [];
  // let imagestream;

  // 해당 파일이 존재하는지 확인
  if (fs.existsSync(imagePath)) {
    //ocr 결과 저장
    ocr_python(id, filename, (resultArray) => {
      //실행 결과 출력
      console.log(`출력값 ${resultArray}`);

      getImageFromMySQL(
        resultArray,
        (error, item_name, imageStream, EE, UD, NB) => {
          if (error) {
            // 오류 처리
            console.error('MySQL에서 이미지를 가져오는 중 오류 발생:', error);
            res.status(404).send(error);
            return;
          }

          if (imageStream) {
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
                filename: item_name,
                image64: image64, // Base64로 인코딩된 이미지 데이터
                EE: EE, // 기타 정보
                UD: UD,
                NB: NB,
              };
              // JSON 객체 응답
              res.json(responseData);
            });
          }

          // if (imageStream) {
          //   // 이미지를 응답으로 스트리밍합니다.
          //   imageStream.pipe(res); // `res`가 HTTP 응답 객체를 나타내는지 확인하세요.
          // }
          else {
            // 이미지를 찾지 못한 경우
            console.log('해당 OCR 텍스트에 대한 이미지를 찾을 수 없습니다.');
            res.status(404).send('이미지를 찾을 수 없습니다.');
            // 이미지를 찾지 못했다는 것을 나타내는 응답을 보낼 수 있습니다.
          }
        }
      );
    });
  } else {
    // 파일이 없는 경우 404 에러 반환
    res.status(404).send('이미지를 찾을 수 없습니다.');
  }
});

//#region ocr_python / select_SQL
function ocr_python(id, filename, callback) {
  const pythonProcess = spawn('python', [
    'Python/OCR/OCR_Image.py',
    `./uploads/${id}/${filename}`,
  ]);

  let arr = ''; // 결과를 저장할 배열

  pythonProcess.stdout.on('data', (data) => {
    // 파이썬 스크립트의 출력을 받아서 Node.js에서 처리
    // const result = JSON.parse(data); // JSON 형식의 데이터를 파싱하여 배열로 변환
    // arr.push(...result); // 결과 배열에 추가
    arr += data;
  });

  pythonProcess.stderr.on('data', (data) => {
    // 오류 발생 시 처리
    console.error(`Python 스크립트 실행 중 오류 발생: ${data}`);
  });

  pythonProcess.on('close', () => {
    // 파이썬 스크립트 실행 완료 시 처리
    callback(arr);
  });
}

//***** 수정 필요 ******//

// OCR 결과를 받아와서 MySQL에서 이미지를 가져오는 함수
function getImageFromMySQL(ocr_arr, callback) {
  let query;

  // if (ocr_arr.length === 2) {
  //   query = `SELECT item_name, image, EE, UD, NB FROM pill_info WHERE (front_mark = '${ocr_arr[0]}' AND back_mark = '${ocr_arr[1]}') OR (front_mark = '${ocr_arr[1]}' AND back_mark = '${ocr_arr[0]}');`;
  // } else if (ocr_arr.length === 1) {
  //   query = `SELECT item_name, image, EE, UD, NB FROM pill_info WHERE front_mark = '${ocr_arr[0]}' OR back_mark = '${ocr_arr[0]}';`;
  // }
  query = `SELECT item_name, image, EE, UD, NB FROM pill_info WHERE front_mark = '${ocr_arr}' AND BACK_MARK = 'LT';`;

  // MySQL 쿼리 실행
  connection.query(query, (error, results, fields) => {
    if (error) {
      console.error('MySQL 쿼리 실행 중 오류 발생:', error);
      callback(error, null, null, null, null); // 오류를 콜백에 전달
      return;
    }
    if (results.length > 0) {
      const item_name = results[0].item_name;
      const imageBuffer = results[0].image;
      const EE = results[0].EE;
      const UD = results[0].UD;
      const NB = results[0].NB;

      // 이미지 데이터를 스트림으로 변환
      const imageStream = Readable.from(imageBuffer);

      callback(null, item_name, imageStream, EE, UD, NB); // 스트림과 필드들을 콜백에 전달
    } else {
      console.log('해당 OCR 텍스트에 대한 이미지를 찾을 수 없습니다.');
      callback(null, null, null, null, null); // 이미지를 찾지 못한 경우 null을 콜백에 전달
    }
  });
}

//#endregion

//#region  DB(Join, Login, CheckID)
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
