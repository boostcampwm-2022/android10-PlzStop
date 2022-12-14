# android10-PlzStop

![cover2](https://user-images.githubusercontent.com/48354989/205480662-4a958899-33a4-406b-87c1-2c16723a43e5.png)

## 버스 🚌, 지하철 🚋 멈춰! ⛔ 

> 혹시 **막차 시간** 확인을 위해<br/>
계속 핸드폰만 붙잡고 계시진 않으신가요? <br/>
**Plz Stop**이 대신 확인해드릴게요!

### 🚌 원하는 경로의 **대중교통 막차 정보**를 확인해 보세요

출발지와 도착지를 입력하면 해당 경로의 대중교통 막차 정보를 제공합니다

### ⏰ 막차 알람을 등록해 보세요

해당 경로의 막차 알람을 등록하면 막차 시간 00분 전에 알람을 통해 알려드립니다

### 🏃🏻 정류장까지 막차시간보다 먼저 도착할지 시합해보세요

사용자의 현재 위치와 막차시간을 실시간으로 보여주며 사용자가 이동해야할 경로를 안내합니다

### [앱 실행해보러 가기](https://github.com/boostcampwm-2022/android10-PlzStop/releases/tag/v1.0.0)

## 팀 소개 🧑‍🤝‍🧑 
| K008 김시진 | K037 이종성 | K039 이지민| K048 조경현|
|:-----------:|:----------:|:----------:|:----------:|
|<img src="https://user-images.githubusercontent.com/74500793/200560529-5c77f1a6-bcdc-4517-a13f-1f274683f530.png" width="150" height="150">|<img src="https://user-images.githubusercontent.com/74500793/200560658-e61ebec8-5e5d-42cf-9a65-a9f34bbebde7.png" width="150" height="150">|<img src="https://user-images.githubusercontent.com/74500793/200560030-6b96b399-e1c0-40d9-8901-2a959d437ab5.png" width="150" height="150">|<img src="https://user-images.githubusercontent.com/74500793/200560802-28af2528-a1e9-48cb-9e5e-889793bb53bb.png" width="150" height="150">|
|[@koreatlwls](https://github.com/koreatlwls)| [@DoTheBestMayB](https://github.com/DoTheBestMayB) |[@jeeminimini](https://github.com/jeeminimini)|[@khcho226](https://github.com/khcho226)|

</br>

# 주요 기능

## 🗺️ 지도
- 사용자의 현재 위치를 **실시간으로 트래킹**하여 보여줍니다.
- 지도상의 임의의 지점을 클릭하면 **현 위치로부터의 거리를 포함한 상세 정보**를 보여줍니다.
- 막차 알람이 설정되어 있으면 화면 하단에 **알람 정보**를 보여줍니다.

|지도|지도|
|:------:|:-----:|
| <img width="200" src="https://user-images.githubusercontent.com/61337202/207513688-f2beffbc-046c-4005-affe-69fe2f6120f0.gif"> | <img width="200" src="https://user-images.githubusercontent.com/61337202/207513754-5e50e63a-1b1d-49f6-9f4a-b94c784f2699.gif"> |

## 🔍 검색
- 원하는 장소를 검색할 수 있습니다.
- 목적지에 갈 수 있는 대중교통 경로를 알려줍니다.

|검색|검색|검색|
|:------:|:-----:|:-----:|
| <img width="200" src="https://user-images.githubusercontent.com/61337202/207515368-9e24608f-31f7-426b-80e0-7867f9da2e30.gif"> | <img width="200" src="https://user-images.githubusercontent.com/61337202/207515372-c589c3e5-538d-4090-a578-887ca149537e.gif"> | <img width="200" src="https://user-images.githubusercontent.com/61337202/207515376-35af8019-c240-46a9-b9a4-680fb83a6d37.gif"> |

## 🚌 막차
- 경로상에 있는 승차지의 막차 시간을 알려줍니다.
- 승차지 사이의 이동 거리와 막차 시간을 고려하여, 첫 승차지에 탑승하러 출발해야 하는 찐-막차 시간을 알려줍니다.

|막차|막차|
|:------:|:-----:|
| <img width="200" src="https://user-images.githubusercontent.com/61337202/207516539-b8f4fe17-f119-4bee-a3f4-266baaebbd13.gif"> | <img width="200" src="https://user-images.githubusercontent.com/61337202/207520444-4bbe104b-74da-474d-bb15-4326de3edc04.gif"> |

## ⏰ 알람
- 사용자가 원하는 경로의 막차 시간 00분 전에 알람을 설정할 수 있습니다.
- 알람을 소리 또는 진동으로 선택할 수 있습니다.

|알람|알람|
|:------:|:-----:|
| <img width="200" src="https://user-images.githubusercontent.com/61337202/207517640-86b73e4a-3b2e-4c2c-9b79-966e09264fc1.gif"> | <img width="200" src="https://user-images.githubusercontent.com/61337202/207517641-f73be4a2-db02-46ff-a1cb-0518388ce27e.gif"> |

## 🏃🏻 미션
- 사용자의 현재 실시간 위치를 보여줍니다.
- 목적지까지의 경로를 표시해줍니다.
- 막차시간보다 먼저 도착할지 시합할 수 있습니다.

|미션|미션|
|:------:|:-----:|
| <img width="200" src="https://user-images.githubusercontent.com/61337202/207521798-682d24d8-197c-4e9a-ab02-bad3ec67f26c.gif"> | <img width="200" src="https://user-images.githubusercontent.com/61337202/207518265-273d3893-f0c9-424e-9b8e-0aaec3174b65.gif"> |

</br>

# 기술 스택

> Clean Architecture
> 
- UseCase를 이용해 기능 직관적 판단 가능
- 새로운 기능이 추가되거나 내부 로직이 변경되어야 할 때 유연하게 대처 가능

> Multi Module
> 
- 수정된 모듈만 빌드 → 빌드 시간 단축
- 의존성이 낮아질 수 있다.

> Hilt
> 
- @AndroidEntryPoint를 사용하여 Service, BroadCastReceiver에도 의존성 주입 가능
- 프로젝트 설정의 간소화
- 쉬운 모듈 탐색과 통합

> Navigation
> 
- Safe Args
- Activity보다 가벼운 Fragment
- 쉬운 화면 전환 Animation 추가

> `Moshi` vs Gson
> 
- 직렬화 실패 메시지 제공
- 다형성 데이터 직렬화 제공
- Codegen 방식

> `T Map` vs 타 Map SDK
> 
- T Map 대중교통 API와의 원활한 데이터 연동을 위해 사용
- 벡터 맵(Vector Map) 지원
- 다른 지도 어플에 비해 깔끔한 UI
- 타 Map SDK에 비해 용량이 적음

> `DataStore` vs Sharedpreference
> 
- DataStore는 코루틴과 Flow를 통해 읽고 쓰기에 대한 비동기 API를 제공
- DataStore는 UI 쓰레드를 호출해도 안전
- Runtime Exception으로부터 안전

> `Foreground Service` vs WorkManger
> 
- 둘 다 즉시 실행해야하는 작업에 사용
- WorkManager의 경우 상황에 따라 지연 가능
- 사용자의 경로를 지속적으로 보여주며 UI를 변경해야하기 때문에 Foreground Service 사용

