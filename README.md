# 🐾 TagPaw
> NFC 태그를 활용하여 반려동물 분실 시 보호자에게 빠르게 연락할 수 있도록 만든 앱
<br>

## 📱 주요 기능
| 화면 | 설명 |
|------|------|
| 홈 | 등록된 반려동물 목록 조회 |
| 반려동물 등록 | 이름, 나이, 비상 연락처, 메모, PIN 입력 |
| NFC 태그 등록 | 입력한 정보를 NFC 태그에 저장 |
| 정보 수정 | 등록된 반려동물 정보 수정 및 재태깅 |
<br>

## 🛠 기술 스택
| 분류 | 기술 |
|------|------|
| Language | Kotlin |
| UI | Jetpack Compose |
| Architecture | MVVM + Layered Architecture |
| DI | Hilt |
| Database | Room |
| Hardware | NFC (NDEF, ReaderMode) |
| Async | Coroutines |
<br>

## 🏗 아키텍처
MVVM + Layered Architecture

com.example.tagpaw  
├── data  
│   ├── repository  
│   └── roomdb  
├── domain  
│   └── entities  
├── nfc  
│   └── NfcUtils  
├── navigation  
└── ui (Presentation)  
    ├── home  
    ├── addpet  
    ├── detail  
    ├── tag  
    └── emergency  
<br>

## 💡 문제 해결 과정

### NDEF 멀티 레코드를 활용한 데이터 구조 설계
- NFC 태그의 제한된 저장 공간 안에서 행인용 정보와 보안용 정보를 함께 저장해야 하는 문제가 있었습니다.
- SMS URI 레코드와 PIN 해시 텍스트 레코드를 하나의 NDEF Message에 저장하는 멀티 레코드 구조를 적용하여 공개 정보와 보안 정보를 분리했습니다.

### NFC ReaderMode 기반 태그 인식 안정성 개선
- 일반 Intent 기반 NFC 수신 방식은 앱 사용 중 다른 NFC 앱이 우선 실행될 수 있는 문제가 있었습니다.
- `enableReaderMode()`를 적용하여 앱이 Foreground 상태일 때 NFC 이벤트를 직접 수신하도록 구현했습니다.
- `DisposableEffect`를 활용하여 화면 진입 시 ReaderMode를 등록하고 종료 시 자동 해제하도록 구성했습니다.

### SHA-256 기반 PIN 검증
- NFC 태그 수정 시 누구나 정보를 덮어쓸 수 있는 문제를 방지하고자 했습니다.
- PIN을 SHA-256으로 해시화하여 저장하고, 수정 요청 시 해시값 비교를 통해 태그 소유자를 검증하도록 구현했습니다.
- 이를 통해 태그 데이터의 무단 변경을 방지하는 검증 구조를 적용했습니다.
<br>

## ✍️ 관련 포스트
- [Compose 반려동물 NFC 태그 앱, TagPaw 기록](https://velog.io/@kimsw215/Android-Compose-%EB%B0%98%EB%A0%A4%EB%8F%99%EB%AC%BC-NFC-%ED%83%9C%EA%B7%B8-%EC%95%B1%EC%95%B1-TagPaw-%EA%B8%B0%EB%A1%9D)
- [NFC 태그 구현 과정에서 마주한 두 가지 트러블슈팅](https://velog.io/@kimsw215/NFC-%ED%83%9C%EA%B7%B8-%EA%B5%AC%ED%98%84-%EA%B3%BC%EC%A0%95%EC%97%90%EC%84%9C-%EB%A7%88%EC%A3%BC%ED%95%9C-%EB%91%90-%EA%B0%80%EC%A7%80-%ED%8A%B8%EB%9F%AC%EB%B8%94%EC%8A%88%ED%8C%85)
