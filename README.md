# OCR과 이미지 다중 분류를 활용한 의약품 정보 제공 APP

이 프로젝트는 알약 사진을 촬영하면 OCR(광학 문자 인식) 및 이미지 다중 분류 기술을 활용하여 해당 알약의 정보 제공하는 안드로이드 애플리케이션입니다. 
사용자는 알약의 사진을 찍어 알약 정보를 검색할 수 있으며, 이를 통해 의약품의 효능, 용법·용량, 주의사항 등의 정보를 빠르게 확인할 수 있습니다.

## 📌목표
- 기존의 의약품 검색 방식(사용자가 직접 입력하는 방식)의 불편함을 해소
- 노년층을 포함한 사용자가 쉽게 의약품 정보를 검색할 수 있도록 지원
- 의약품 오복용 방지 및 의료 사고 예방
  
## 📋 주요 기능
1. **OCR 기반 알약 정보 인식**
  - Google Cloud Vision API를 활용하여 알약 외형 정보(색상, 모양, 문자 등) 추출
   
2. **이미지 다중 분류 모델 적용**
  - 머신러닝 모델(CNN) 활용하여 의약품의 모양, 색상, 크기를 분류
  - 데이터베이스와 연동하여 식별한 알약에 대한 용법용량, 효능효과, 주의사항 등 관련된 주요 정보를 제공
   
3. **서버-클라이언트 연동**
  - Node.js 서버와 MySQL 데이터베이스를 연동하여 실시간 데이터 관리
  - 사용자는 안드로이드 앱에서 검색 요청, 서버는 의약품 정보를 조회 후 응답


## 🛠 사용 기술
## 📱 안드로이드 앱
- Android Studio(Java): 애플리케이션 UI 및 기능 구현
- Retrofit 라이브러리: RESTful API와의 데이터 통신
  
## 🌐 서버
- Node.js (Express 프레임워크): 서버 및 API 구현
- MySQL: 사용자 및 의약품 데이터 관리
- Google Cloud Vision API: OCR을 활용한 문자 데이터 추출
- Python : 이미지 다중 분류 모델 학습 및 예측, VGGNet 기반 CNN 모델 사용
  
## 🏆 기대 효과
- 노년층 사용자의 약물 오복용 및 부작용 방지
- 알약 기능을 통한 의약품 구별 용이성 제공
- OCR 및 이미지 분석 기술을 활용한 의료 분야 AI 응용 사례

## 🔗 참고 자료
- [Google Cloud Vision API](https://cloud.google.com/vision/docs/ocr)
- [공공데이터-식약처](https://nedrug.mfds.go.kr/pbp/CCBGA01/getItem?totalPages=4&limit=10&page=2&&openDataInfoSeq=11)

