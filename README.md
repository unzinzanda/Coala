# 싸피인간 코딩장비 공유 서비스 - COALA
> 영화에 대한 커뮤니티와 위시리스트, 검색을 통해 영화 추천 기능을 갖춘 사이트

<br>

### 프로젝트 기간 🗓️
- 2023.07.04 ~ 2023.08.18

<br>

### 개발환경 🛠️
>
> Java 17.
> >
> Spring Boot 3.1.2.
> > 
> Node.js 18.
> >
> Next 13.2.4.
> >
> React 18.2.0.

<br>

## 0. 팀원 🧑‍🤝‍🧑

| 팀원| 역할 | 담당 | 파트 |
|:---|:---|:---|:---|
|정선재|팀장| Front End | |
|심은진|팀원| Front End | |
|서지호|팀원| Front End | |
|강승현|팀원| Back End | 인프라 구축, 멤버 관리, 거래 게시판 | 
|권민우|팀원| Back End | 커뮤니티 게시판, 디버깅, 코드 테스트 |
|김수찬|팀원| Back End | 인프라 구축, 채팅I/O, 거래 및 계약 |

### 0.1 상세 업무 분담 내역
---
<br>

**공통**: 
- 데이터베이스 모델링 
- 컴포넌트 구조 분석
- 시스템 목업

**정선재**: 
| 개발 분야| 역할 |
|:---:|:---|
|BE| - Django 서버 구현<br> - 영화 리뷰에 대한 데이터 베이스 관리<br> - 사용자의 캐시데이터 기반의 영화 추천 알고리즘 제작 <br> - 코사인유사도를 활용한 영화 추천 알고리즘 제작|
|FE| - 사용자와 관련된 CRUD 서비스 구현<br> - 댓글 페이지 및 댓글창 구현|

**심은진**:
| 개발 분야| 역할 |
|:---:|:---|
|BE| - 영화 정보에 대한 데이터 관리<br> - 코사인유사도를 활용한 영화 추천 알고리즘 제작|
|FE| - Vue 클라이언트 구현 <br> - 로그인 페이지 디자인<br> - 메인페이지 디자인 <br> - 세부 팝업 디자인|

**서지호**: 
| 개발 분야| 역할 |
|:---:|:---|
|BE| - Django 서버 구현<br> - 영화 리뷰에 대한 데이터 베이스 관리<br> - 사용자의 캐시데이터 기반의 영화 추천 알고리즘 제작 <br> - 코사인유사도를 활용한 영화 추천 알고리즘 제작|
|FE| - 사용자와 관련된 CRUD 서비스 구현<br> - 댓글 페이지 및 댓글창 구현|

**강승현**:
| 개발 분야| 역할 |
|:---:|:---|
|BE| - 영화 정보에 대한 데이터 관리<br> - 코사인유사도를 활용한 영화 추천 알고리즘 제작|
|FE| - Vue 클라이언트 구현 <br> - 로그인 페이지 디자인<br> - 메인페이지 디자인 <br> - 세부 팝업 디자인|

**권민우**: 
| 개발 분야| 역할 |
|:---:|:---|
|BE| - Django 서버 구현<br> - 영화 리뷰에 대한 데이터 베이스 관리<br> - 사용자의 캐시데이터 기반의 영화 추천 알고리즘 제작 <br> - 코사인유사도를 활용한 영화 추천 알고리즘 제작|
|FE| - 사용자와 관련된 CRUD 서비스 구현<br> - 댓글 페이지 및 댓글창 구현|

**김수찬**:
| 개발 분야| 역할 |
|:---:|:---|
|BE| - 영화 정보에 대한 데이터 관리<br> - 코사인유사도를 활용한 영화 추천 알고리즘 제작|
|FE| - Vue 클라이언트 구현 <br> - 로그인 페이지 디자인<br> - 메인페이지 디자인 <br> - 세부 팝업 디자인|


<br>

## 1. 구조도

### 1.1. 아키텍쳐 구조 🔗
![이미지 추가해주세요]()

---
### 1.2. ERD 구조 🎛
![이미지 추가해주세요]()

---
### 1.3. 컴포넌트 구조 🎞
![이미지를 추가해주세요]()


<br>

### 1.4. 폴더 구조
---
[파일 구조도 위치](./docs/File_Content/README.md)


<br>

## 2. 개요 📑

### 2.1 목표 서비스
```
    1. 목표 서비스 작성
    2. 목표 서비스 작성
    3. 목표 서비스 작성
```
### 2.2 상세 네용
> 0. 시작페이지
```
1. 동적 화면 생성
2. Django Rest-Auth를 활용한 회원가입 / 로그인
3. 로그인 정보에 따른 Navbar 표시 변경
```

> 1. 메인페이지
```
1. 인기 영화에 대한 티저 영상 제공
2. 인기영화 제공
3. 개봉예정작 소개
4. 찜한 목록으로 접근
5. 영화 제공에 대한 시각적인 부분 개선
```

> 2. 영화 추천 서비스
```
1. 유저의 찜 목록을 기반으로 한 영화 추천
2. 영화 장르를 기반으로 한 영화 추천
3. 추천 된 영화목록 중 하나를 랜덤 추출 후 영상 제공
```

> 3. 영화 조회 서비스
```
1. 영화 제목을 기반으로 한 영화 조회
2. 영화 장르를 기반으로 한 영화 조회
3. 개봉 연도를 기반으로 한 영화 조회
4. 검색어에 대한 영화의 유사도를 기반으로 한 영화 조회
5. 검색한 영화와의 유사도를 기반으로한 영화 조회
```

> 4. 커뮤니티 서비스
```
1. 영화 리뷰 및 별점 생성
2. 영화별 리뷰에 대한 접근성을 높임 
3. 게시글을 통해 타 유저의 프로필 확인
4. 리뷰활동을 통해 평점 및 댓글 생성을 통한 의사소통 구현
```

> 5. 개인 프로필 페이지
```
1. 개인간 팔로우 팔로잉 기능
2. 사용자가 찜한 영화 목록 조회
3. 사용자가 생성한 게시글 조회
4. 비밀번호 수정 및 회원탈퇴 기능
```

<br>

## 3. 웹 사이트 예시 📺

### **시작 페이지**


<br>

### **메인 페이지**


<br>

### **마이페이지**


<br>


### **자유게시판 및 테크게시판**


<br>

### **제공자 게시판**


<br>

### **이용자 게시판**


<br>

### **채팅**


<br>

### **계약서 작성**


<br>



[def]: './docs/'