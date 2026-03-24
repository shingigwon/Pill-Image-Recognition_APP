# 💊 OCR과 이미지 다중 분류를 활용한 알약 정보 제공 APP  
> (2024.03 ~ 2024.06)

---

## 📌 프로젝트 소개
알약 사진을 촬영하면 OCR(광학 문자 인식)과 이미지 다중 분류 기술을 활용하여  
해당 알약의 정보를 제공하는 안드로이드 애플리케이션입니다.  

사용자는 알약 사진만으로 효능, 용법·용량, 주의사항 등의 정보를 빠르게 확인할 수 있습니다.

---

## 🎯 프로젝트 목표
- 기존 텍스트 기반 검색의 불편함 해소  
- 노년층도 쉽게 사용할 수 있는 직관적인 검색 방식 제공  
- 알약 오복용 방지 및 의료 사고 예방  

---

## 🚀 주요 기능

### 🔍 OCR 기반 알약 정보 인식
- Google Cloud Vision API를 활용하여 알약의 문자 및 외형 정보 추출  

### 🧠 이미지 다중 분류 모델 적용
- 머신러닝 모델(CNN) 기반 모델을 활용하여 알약의 모양, 색상, 크기 분류  
- DB와 연동하여 알약의 효능, 용법·용량, 주의사항 정보 제공  

### 🌐 서버-클라이언트 연동
- Node.js 서버와 MySQL DB를 활용한 실시간 데이터 처리  
- Android 앱에서 요청 → 서버에서 조회 후 응답 구조 구현  

---

## 🧩 시스템 흐름
촬영 이미지 → OCR(문자 추출) → 이미지 분류(CNN) → 서버 전송 → DB 매칭 → 알약 정보 제공  

---

## 🔍 인식 결과 예시
| OCR 추출 텍스트 | 이미지 분류 결과 |
|----------|----------|
<img width="275" height="130" alt="pill_ocr_text" src="https://github.com/user-attachments/assets/0188f4d3-44f3-44b5-8cae-1a9c96e7e667" />|<img width="435" height="213" alt="image" src="https://github.com/user-attachments/assets/f947bbba-bc8e-4361-98f1-ed4e3c91d4a3" />|

---

## 📱 구현 결과
| 분석 결과 | 검색결과（기본정보）| 검색결과（효능효과）| 검색결과（주의사항) |
| --- | --- | --- | --- |
 <img width="131" height="668" alt="검색결과select" src="https://github.com/user-attachments/assets/e8bd4470-3431-45bc-8d12-8659a22efb79" />| <img width="141" height="678" alt="결과tap1" src="https://github.com/user-attachments/assets/dade53db-8bde-425d-82c9-3bb5a0af9ca1" />|<img width="141" height="678" alt="결과tap2" src="https://github.com/user-attachments/assets/a49dbd45-1087-4089-89be-75fdbafb3a4b" />|<img width="141" height="678" alt="결과tap3" src="https://github.com/user-attachments/assets/52624a76-3d4b-41ac-bce4-71711e65b22f" />|

---

## ⚙️ Tech Stack

### 📱 안드로이드 앱
- Android Studio(Java): 애플리케이션 UI 및 기능 구현
- Retrofit 라이브러리: RESTful API와의 데이터 통신
  
### 🌐 서버
- Node.js (Express 프레임워크): 서버 및 API 구현
- MySQL: 사용자 및 알약 데이터 관리
- 
### 🤖 AI
- Google Cloud Vision API: OCR을 활용한 문자 데이터 추출
- Python : 이미지 다중 분류 모델 학습 및 예측, VGGNet 기반 CNN 모델 사용

---

## 🙋‍♂️ 담당 역할
- Google Vision API를 활용한 OCR 기능 구현  
- CNN 기반 이미지 분류 모델 적용 및 서버 연동  
- Node.js + MySQL 기반 API 서버 설계 및 구현  
- Android UI구현, 서버 간 REST API 통신 구현 (Retrofit)  

---

## 💡 기대 효과
- 노년층 사용자의 약물 오복용 및 부작용 방지  
- 알약 구별의 편의성 향상  
- OCR 및 이미지 분석 기술의 의료 분야 활용 사례  

---

## 🔗 참고 자료
- [Google Cloud Vision API](https://cloud.google.com/vision/docs/ocr)  
- [식약처 공공데이터](https://nedrug.mfds.go.kr/pbp/CCBGA01/getItem?totalPages=4&limit=10&page=2&&openDataInfoSeq=11)
