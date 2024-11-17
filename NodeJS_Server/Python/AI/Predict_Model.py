from keras.api.preprocessing.image import img_to_array
from keras.api.models import load_model
import numpy as np
# import imutils
import pickle
import cv2
import json
import sys


def Predict_Image(img_file):
    model_arr = []
    
    # 이미지를 바이트 버퍼로 읽기
    with open(img_file, 'rb') as f:
        image_buffer = f.read()

    # 바이트 버퍼로부터 이미지를 NumPy 배열로 변환
    nparr = np.frombuffer(image_buffer, np.uint8)
    image = cv2.imdecode(nparr, cv2.IMREAD_COLOR)

    # 이미지 전처리
    image = cv2.resize(image, (96, 96))
    image = image.astype("float") / 255.0
    image = img_to_array(image)
    image = np.expand_dims(image, axis=0)
    

    # 학습된 모델과 LabelBinarizer 로드
    model = load_model('Python/AI/multilabel_model.h5')
    with open('Python/AI/labelbin.txt', "rb") as f:
        mlb = pickle.load(f)

    # 이미지 분류
    proba = model.predict(image, verbose=0)[0]
    idxs = np.argsort(proba)[::-1][:4]
    # 분류된 라벨 출력
    for (i, j) in enumerate(idxs):
        # label = "{}: {:.2f}%".format(mlb.classes_[j], proba[j] * 100)
        # print(label)
        model_arr.append(mlb.classes_[j])

    return json.dumps(model_arr)

    # # Matplotlib를 사용하여 결과 이미지 출력
    # plt.imshow(cv2.cvtColor(output, cv2.COLOR_BGR2RGB))
    # plt.axis('off')  # 축 제거
    # plt.show()


if __name__ == "__main__":
    print(Predict_Image(sys.argv[1]))
    


