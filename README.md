# MadCamp_week2

## 🚀 슬로건

<aside>
💡 카이스트 공연 예약 시스템 Drop!

</aside>

## 👩‍💻👨‍💻 개발 팀원

- [김성아](https://www.notion.so/382c2e0466fd4d999ba930f53312be6a?pvs=21) : 한양대학교 컴퓨터소프트웨어학부 20학번
- [안호진](https://www.notion.so/a0c69196dd9a4b6dbcc9dd52c77b9dbc?pvs=21) : KAIST 전기및전자공학부 22학번

## ⚙️ 개발 환경

### 프론트엔드

- Language: Kotlin
- OS: Android
- IDE: Android Studio
- Target Decive: Galaxy S10e

### 백엔드

- Language: Python
- Framework: Flask
- Database: MySQL

## ℹ️ 애플리케이션 설명

## 주요 기능

- 카이스트 내의 버스킹 정보를 제공합니다. 지도를 통해 당일의 버스킹 정보를 알 수 있습니다.
- 날짜 별로 전체 버스킹 정보를 볼 수 있습니다. 달력을 통해 해당 날짜로 이동할 수 있습니다.
- 버스킹 일정을 즐겨찾기에 등록할 수 있습니다. 클릭했을 때 자세한 정보를 볼 수 있습니다.
- 버스킹 일정을 등록할 수 있습니다. 버스킹 공연들 사이에 일정이 겹치지 못하게 조율합니다.
- 자체 및 카카오 로그인을 지원하며 마이 페이지에서 정보를 수정할 수 있습니다. 로그아웃 및 탈퇴 기능도 지원합니다.
- 마이 페이지에는 버스킹 공연을 얼마나 관람 했는지에 따라 레벨을 높일 수 있고 즐겨찾기에 등록한 공연들만 모아서 한 눈에 볼 수 있습니다.

## 1 로그인

### 1.1 주요기능

- 자체 로그인과 카카오 로그인을 제공하고 자동 로그인 기능이 있습니다.
- 자체 회원가입시에 사진, 이름, 아이디, 비밀번호를 입력받고 비밀번호는 **암호화**하여 서버와 통신합니다.
- 카카오 로그인을 선택했을 때는 카카오에 등록한 정보를 바탕으로 회원가입이 됩니다.
- 서버 측에서 sql injection을 막는 로직을 구현하여 보안을 유지할 수 있습니다.

### 1.2 기술 설명

**FE**

- SHA256 `hashing`을 이용해서 비밀번호를 암호화합니다.
- 서버에 POST 방식으로 요청을 보내서 데이터 베이스에서 검색 후 로그인 성공 및 실패 응답을 받습니다.
- 같은 방식으로 회원가입 진행 시에 데이터 베이스에 새로운 정보를 저장합니다.
- 자동 로그인은 `sharedpreferences`로 구현하였습니다.
- 카카오 sdk를 이용해서 카카오에 저장된 정보를 받아온 후 서버에 요청을 보냅니다.

**BE**

- 데이터 베이스에 연결하여 쿼리문을 실행시킵니다.
- parameterization을 이용하여 sql injection을 방어합니다.
- api 목록
    - login/inserver
    - login/withkakao
    - store_data

## 2 홈

### 2.1 주요 기능

- 오늘의 버스킹 목록을 보여줍니다.
- 지도에 있는 핑을 통해 위치를 알 수 있고 아래에 있는 카드를 통해 버스킹의 정보를 알 수 있습니다.
- 핑을 선택했을 때 해당 카드 위치로 자동 이동합니다.

### 2.2 기술 설명

**FE**

- 카카오 맵 v2 api를 사용하여 지도를 보여줍니다.
- 데이터 베이스에서 받아온 오늘의 공연 정보를 통해 위치를 알아내서 `label` 기능을 이용하여 핑을 보여줍니다.
- 핑을 선택했을 때 `clicklistener`를 구현하여 해당 아이템의 카드 뷰로 가게 합니다. 또한 핑의 모양과 색상을 바꿔주기 위해 기존 `label`을 제거하고 새로운 `label`을 할당하여 로드합니다.
- 핑을 선택했을 시에 해당 위치로 지도의 `camera`를 이동시킵니다.
- 카드 뷰를 수평으로 두어 스크롤 할 수 있게 합니다.
- 즐겨찾기 추가와 자세히 보기 `onclick` 기능을 이용합니다.

**BE**

- 오늘의 공연 정보를 받아옵니다.
- 해당 버스킹이 즐겨찾기에 등록된 것인지 아닌지를 나누어 제공합니다.
- 즐겨찾기에 등록할 수 있게 데이터베이스에 접근합니다.
- api 목록
    - get_today_busking
    - isitfavorite

## 3 공연 일정

### 3.1 주요 기능

- 현재 데이터 베이스에 등록된 모든 공연 일정을 보여줍니다.
- 데이터 베이스에 저장돼있던 날짜를 기준으로 분류하여 정렬합니다.
- 즐겨찾기를 등록해 둔 공연은 공연 일정 탭에서도 확인할 수 있습니다.
- 빈 하트를 눌렀을 때 즐겨찾기에 등록하고 하트가 채워집니다.
- 채워진 하트를 다시 누르면 즐겨찾기에서 삭제하고 하트가 빈 하트가 됩니다.
- 카드를 선택하면 자세한 정보를 볼 수 있습니다.

### 3.2 기술 설명

**FE**

- `Recycler View`로 공연 일정을 가져옵니다. 이 때 중간에 날짜를 넣는 `item`을 추가로 지정하여 날짜 `item`을 띄운 후 해당 날짜의 개수를 통해 카드를 띄웁니다.
- 서버에서 정보를 받아와서 사진, 제목, 장소, 시간을 띄우고 로그인된 사용자의 즐겨찾기에 추가돼있는지를 확인하여 즐겨찾기 정보도 띄웁니다.

**BE**

- `Recycler View`에 전달해줘야하는 정보를 구하는 알고리즘을 동작시킵니다. 예약된 내역들을 한바퀴 스캔하며 Recycler 중간 들어가는 날짜 정보를 담은 item이 추가되어 변하는 Position과, 좋아요 내역에 따른 아이콘 변화의 정보를 서버에서 Linear한 시간에 계산하여 사용자에게 전달하여 쾌적한 사용자 경험을 제공합니다.
- 버스킹 공연 정보를 가져오는 기능을 구현하였습니다.
- api 목록
    
    get_all_busking
    

## 4 일정 등록

### 4.1 주요 기능

- 새로운 공연 일정을 등록할 수 있습니다.
- 공연 대표 이미지와 밴드명, 공연명, 날짜, 시각, 장소, 셋리스트를 입력받습니다.
- 날짜는 달력 dialog을 띄워서 선택할 수 있으며 시작 시각과 종료 시각은 시계 dialog로 선택합니다.
- 공연 장소는 카이스트에서 버스킹을 할 수 있는 장소 중 선택할 수 있습니다.
- 오늘 날짜 이후로만 공연을 등록할 수 있게 제한해뒀습니다.
- 종료 시각이 시작 시각보다 빠르거나 시작 시각이 종료 시각보다 느리지 않게 입력받도록 제한해뒀습니다.
- 데이터베이스에 이미 저장된 공연과 같은 장소, 같은 날짜, 시각에 새로운 공연을 등록하려 시도하면 서버 쪽에서 검사하여 조율합니다.

### 4.2 기술 설명

**FE**

- 공연 날짜를 고를때는 datepicker가, 시간을 고를때는 timepicker가 사용됩니다.
- 공연장소를 선택할때는 카이스트에서 공연이 진행되는 대표적인 장소 7개가 Spinner를 사용해 선택지로 제공됩니다.
- 새로운 일정을 추가할때는 여러가지 예외케이스를 고려하여 올바른 예약을 진행 할수 있도록 유도합니다
    - 과거의 예약 진행 시도
    - 시작 시간보다 종료 시간이 빠른 경우
    - 입력하는 값중 빈칸이 있는 경우
- 공연 대표 사진은 내부 갤러리에서 가져와 사용 할 수 있습니다.

**BE**

- 예약된 정보는 이미지를 제외하고 텍스트 형태로 서버의 데이터베이스에 저장됩니다.
- 저장하기 전에 서버에 저장된 기존의 예약 내역들과 비교하여서 해당 날짜에 해당 장소에 미리 예약을 진행한 사람이 없는지 검사하고 있다면 사용자에게 안내합니다.
- 공연 대표사진은 서버상에 static 폴더에 파일 형태로 저장합니다.
- api 목록
    - store_busking

## 5 마이 페이지


### 5.1 주요 기능

- 데이터베이스에 저장돼있는 정보를 표시하고 수정할 수 있습니다.
- 프로필 수정 페이지에서는 사진, 이름, 비밀번호를 변경할 수 있으며 이 중 수정하고 싶은 필드만 바꾸면 됩니다. 단, 비밀번호 변경은 현재 비밀번호를 반드시 입력해야하며 새 비밀번호와 새 비밀번호 확인 값이 같아야합니다.
- 버스킹 공연 관람 횟수에 따라 레벨을 갖는 기능도 제공하고 싶습니다.
- 로그인한 회원의 즐겨찾기에 등록된 버스킹 목록을 제공합니다. 다른 탭에서와 마찬가지로 여기서도 즐겨찾기 삭제를 할 수 있습니다.

### 5.2 기술 설명

**FE**

- 데이터 베이스에 저장된 정보를 가져옵니다. 프로필 수정 페이지에서도 가져와서 수정하기 전 상태에 보이게 해둡니다.
- 프로필 수정 시에는 입력받은 정보로 데이터베이스를 업데이트하는 api를 호출합니다.
- 좋아요한 버스킹은 일정등록탭에서와 마찬가지로 날짜 순으로 정렬되도록 `adapter`를 구현했습니다.

**BE**

- 데이터베이스에서 저장된 정보를 가공하여 가져옵니다.
- 입력받은 정보로 데이터베이스를 업데이트하는 쿼리문을 실행합니다.
- 즐겨찾기에 등록한 공연 목록만 보냅니다.
- api 목록
    
    mypageprofile
    
    buskinglist/like
