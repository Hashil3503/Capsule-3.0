# 대구가톨릭 대학교 블랜디드매시업 프로젝트
[ Capsule 3.0 ]

처방전 이미지를 촬영하여 처방전을 등록 및 관리하고 자동 알람, 복약 체크 등의 편의 기능을 제공하는 어플리케이션 입니다.

기존 프로젝트( https://github.com/Hashil3503/Capsule )의 개선 버전입니다.


[프로젝트 소개]

프로젝트 이름 : Capsule 3.0

프로젝트 주제 : 처방전 인식 기능을 활용한 처방전 관리 앱

프로젝트 목표 : 기존 프로젝트 개선

진행 기간 : 2025.9.16 ~ 2025.12.1

참여 인원: 김명환, 손준영, 김도은, 김명진

[수행 과정 ]
- 프로젝트 개선 아이디어 도출
- UI/UX 개선
- 챗봇 성능 개선
- 데이터베이스 구조 변경
- 약품 정보 조회 기능 개선
- 리워드 기능 구현
- 약품 도감 기능 구현
- 자동 알람 설정 기능 구현
- 비밀번호 설정 기능 구현
- 반복적인 테스트를 통한 버그 수정

[상세 내용]

주요 개선 사항
- 챗봇 모델 교체 (Llama3.0 기반 Bllossom 모델 -> gpt-4o-mini api)
- 약품 정보 조회 방식 변경 (데이터베이스 조회 기반 -> 약품 정보 조회 api "e약은요" 활용)
- UI/UX 개선 (가독성 및 심미성 향상)
- 리워드 기능 구현 (복약 체크 or 약품 도감 갱신시 리워드 지급)
- 자동 알람 설정 기능 구현

[결과 및 기대 효과]

실행 화면
◯ 로그인 화면

ㄴ<img width="747" height="530" alt="{88D4BF0E-A619-45B0-BE67-0E6D2B19084F}" src="https://github.com/user-attachments/assets/6316676a-f20b-4f74-b8c2-2fef234199eb" />

◯ 메인화면 - 설정(톱니바퀴 버튼) - 기본 알람 시간대 설정

<img width="707" height="517" alt="{59CB8CD6-CB1D-4448-BEF4-7FC0A635BBFB}" src="https://github.com/user-attachments/assets/b48baf22-a2c9-4848-9788-733dd6e82dff" />

◯ 처방전 인식 & 처방전 조회

<img width="696" height="494" alt="{A0300540-1B10-481C-A0F0-7C1B39E7C3CB}" src="https://github.com/user-attachments/assets/91300bfd-94d8-4d40-a918-0f81d7378992" />


<img width="698" height="492" alt="{473671C4-7D47-44C6-AE23-193CF4A055AC}" src="https://github.com/user-attachments/assets/9102c261-72ae-4b3d-8382-ebe0dedea257" />


◯ 복약 알람 자동 설정

<img width="1029" height="440" alt="{CA1CF7B1-4FA1-41C3-8581-E915D4A5D74F}" src="https://github.com/user-attachments/assets/1511ff1d-ce9a-4f12-b0b7-f1045b765d1a" />

◯ 챗봇 & 약품 도감

<img width="612" height="448" alt="{2D168061-9066-4905-82BF-C2500D5A953C}" src="https://github.com/user-attachments/assets/ea4a07e0-c172-4368-90e7-9cc3aebaf139" />



- 기존의 부족한 약품 정보 문제를 "e약은요" api를 통해 보완
- 리워드 기능을 통한 복약 준수 동기 부여
- 약품 도감 기능을 통해 처방전 삭제 이후에도 약품 정보를 지속적으로 조회 가능
- 자동 알람 설정 기능을 통한 복약 편의성 향상
- 비밀번호 설정 기능을 통한 개인정보보호
- 모델 교체를 통한 챗봇 성능 개선

---------------------------------------------------------------------
[개발 환경]

개발 도구 : Android Studio

언어 : JAVA

DBMS : SQLite

라이브러리 : Room, Caemera X, OKHttp, Rrtrofit, OpenCSV, Security-Crypto, Glide

API : ML Kit, e약은요, OpenAI API(gpt-4o-mini)


---------------------------------------------------------------------
