# anabada
# 중고 거래 플랫폼

-   안드로이드 앱

크게 회원가입, 자동로그인, 게시글과 댓글 목록 및 상세페이지 등의 기능과 게시글과 댓글 작성 및 수정, 삭제 등의 기능을 구현하여 더 보기 버튼 클릭 시 팝업 메뉴로 사용할 수 있도록 하고 게시글 상세페이지에서 스크롤 위치에 따라 툴바 색상이 점진적으로 불투명화 되는 등 세부 기능 들까지 공들여 구현했습니다. 또한 이미지 게시글 포스팅 시 이미지를 기준 크기 이하로 리사이 징하고 multipart 형식으로 서버에 전송했으며, 일관된 뷰를 위해 게시글 목록화면이나 게시글 상 세페이지에서는 정사각형으로 크롭 된 썸네일과 이미지를 보이도록 구현하였습니다.
그 외에도 앱과 안드로이드 시스템의 색상 테마 통일과 커스텀 폰트 적용, 버튼 연속 중복 클릭 방지, 상단 툴바 더블클릭 시 맨 위로 스크롤, 회원가입 시 비밀번호 유효성 실시간 체크, 새로운 댓글 작성 시 view coloring하여 실시간으로 새 댓글과 자신이 작성한 댓글이 한눈에 들어오게끔 하는 등 사용자 편의를 위한 소소한 기능들에 신경 쓰며 안드로이드 앱 서비스 기획 및 개발 전 반을 진행하였습니다.
마이페이지, 게시물 검색 등 기능 확장이 계획되고 더 이상 MVC로는 코드 관리 및 확장이 어렵 다고 판단하여 MVVM과 Repository 패턴, Coroutine, 의존성 주입을 위해 Koin을 적용하여 안정화 및 최적화를 진행중입니다. Optimization 브랜치에서 작업 진행중이며, Navigation component를 사용하고 각 탭의 화면을 fragment로 구현, PagingSource와 RemoteMediator 등 최신 라이브러리 로 앱의 사용성과 확장성을 높이고자 하였습니다.
회원시스템 및 자동 로그인 기능을 구현하면서 서버 측과의 사용자 인증 방식을 고민했는데, 서버 통신 때마다 http 헤더를 통해 인증 정보를 쉽게 주고받고 파싱하는 웹과 달리 안드로이드에서는 retrofit 등 라이브러리의 사용이 필요하고 오프라인 사용성을 높이기 위해 서버 통신을 최소화해야 하는 등 제약이 많았습니다. 따라서 보편적으로 서버에 사용자 인증 요청 후 토큰을 발급해주면 이를 헤더에 넣어 서버 통신 시 인증 수단으로 사용하는 방법을 사용하는데, 이미 서버 측에는 웹 프론트엔드와 쿠키를 주고받으며 인증하는 방식이 구현되어 있었고 토큰 인증 로직을 추가 구현하기 어려운 상황이었습니다.
기존 통신 방식을 유지하기 위해 okhttp3 라이브러리의 Interceptor를 사용하고 shared preference에 cookie 및 로그인 기록을 저장하여 서버 통신 시 header에 cookie를 추가해 사용자 세션을 인증했고, 사용자 기기에서 최초 로그인 이후엔 앱 접속 시 자동 로그인이 되도록 구현했습니다. 

## Current

서버 폐쇄로 기능 작동 x
'Optimization' branch에서 MVVM, Repository pattern, Koin 적용 등 리팩토링 중

## demo (Toolbar scroll gradient animation, 접속 시 화면, 게시물 작성 화면, 회원가입  등)
<p align="center">
  <img src="https://user-images.githubusercontent.com/50130497/176151870-b1584704-91f3-437b-bc59-fb7496ff19eb.jpeg" width="23%">
  <img src="https://user-images.githubusercontent.com/50130497/176138529-f2788a62-3d19-45d3-ac5a-37a720bd090a.jpeg" width="23%">
  <img src="https://user-images.githubusercontent.com/50130497/176137486-29549464-c45d-4d14-9321-9abd58682f45.jpeg" width="23%">
  <img src="https://user-images.githubusercontent.com/50130497/176137459-0419633c-9cf4-49c2-bf3b-bb8947dfa516.jpeg" width="23%">
<p/>

<p align="center">
  <img src="https://user-images.githubusercontent.com/50130497/176137341-56a8ae80-47c5-40a3-a0f2-6b2ed456a715.jpeg" width="23%">
  <img src="https://user-images.githubusercontent.com/50130497/176137376-10e22ca2-6971-4e9a-9189-620a0ed62267.jpeg" width="23%">
  <img src="https://user-images.githubusercontent.com/50130497/176137395-508c1a8e-23fb-432f-9652-5cf8dd323d2e.jpeg" width="23%">
  <img src="https://user-images.githubusercontent.com/50130497/176137430-fdb8bda1-89ee-4afe-af0e-15de804d9ef6.jpeg" width="23%">
<p/>

<p align="center">
  <img src="https://user-images.githubusercontent.com/50130497/176138547-c1065d2f-9f5f-4a68-b8c5-46341d9f2421.jpeg" width="23%">
  <img src="https://user-images.githubusercontent.com/50130497/176138557-25aa2a4f-5029-4d80-8f83-7dc69ce10232.jpeg" width="23%">
<p/>

