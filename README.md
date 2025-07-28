DUGAZA : 공공 API 활용 여행 정보 통합 플랫폼

> 공공 API와 연동하여 관광지에 대한 정보 (관광명소, 문화시설, 축제공연행사, 여행코스, 레포츠, 숙박, 쇼핑, 음식점) 에 대한 정보 제공
> 공공 API의 구조적 제약을 해결하며 사용자 경험을 고려한 UI/UX, 인증 시스템, 성능 최적화를  함께 구현해보았습니다.
 
---

## 프로젝트 핵심 요약
- 🧭 **구현 주요 기능**  
  기차/고속버스 실시간 검색, OAuth2 로그인, Spring Security 기반 인증/인가, 관광지 상세 정보 지연로딩

- 🔧 **사용 기술 스택**  
  Spring Boot, Spring Security, MyBatis, AOP, RestClient

- 🔍 **핵심 설계**  
  다중 사용자 인증 구조

- 📈 **성능 개선**  
  RestClient로 WebClient 대비 평균 30% 응답 시간 단축

- 🛡️ **보안 특징**  
  카카오 로그인 + Remember-Me + 다중 Security Filter 분리


## 프로젝트 개요
- **여행을 계획하는 사용자**가 교통편과 관광지를 한 번에 찾을 수 있는 플랫폼
- 공공 API 기반 실시간 정보 제공 + 사용자 인증 구조 내장
- 관광/교통 정보를 단순히 불러오는 것을 넘어, **API의 구조적 제약 해결**에 초점


## 🧠 주요 기술 특징

### 🧭 교통 검색 시스템
- **허브역 기반 기차 검색 알고리즘**
  > 국토교통부 API가 직접 검색을 지원하지 않아, 17개 허브역 간 연결 DB 구조 설계
- **고속버스 정보 실시간 연동**
  > 도시 → 터미널 → 노선 → 시간표 흐름의 UX 설계

### 🔐 인증/보안 구조
- **Member / Seller 역할 분리**
- **다중 SecurityFilterChain**으로 웹/REST API 분리 적용
- **카카오 OAuth2 로그인** + 미가입자 처리 플로우

### 📊 AOP 기반 로깅 시스템
- `@LogExecutionTime` 어노테이션 기반 실행 시간 측정
- 컨트롤러/서비스/매퍼별 로깅 분리 적용

---


## 🧪 기술 스택

| 분야 | 기술                                                      |
|------|---------------------------------------------------------|
| Backend | Spring Boot , Spring Security, Spring AOP               |
| DB | Oracle, MyBatis                                         |
| API 통신 | RestClient, WebClient (비교 후 RestClient 채택)              |
| 인증 | FormLogin, OAuth2 (Kakao), Remember-Me, Spring Security |
| UI | Thymeleaf, Bootstrap, JavaScript                        |
| 기타 | GitHub Actions (배포 자동화 준비), AOP Logging                 |

---


## 🖼️ UI/UX 미리보기

> 더 많은 스크린샷은 [`docs/screenshots.md`](./docs/screenshots.md)에서 확인할 수 있습니다.

| 기차 검색 화면 | 고속버스 검색 화면 |
| ![train](https://github.com/user-attachments/assets/1bd08229-431d-4482-b4fc-b1d46e4b3392) | ![bus](https://github.com/user-attachments/assets/0f8e045e-e033-4e3d-9b12-2ec53814de5a) |
|---|---|



## 🔎 폴더 구조
<pre lang="md"> 
├── controller/
├── service/
├── mapper/
├── aop/
├── security/
├── api/ # API 클라이언트
├── dto/
├── entity/
├── templates/
└── docs/ # 상세 설명 분리
</pre>


## 📎 문서 링크

- [`docs/transportation.md`](./docs/transportation.md) - 허브역 기반 기차 검색 구조
- [`docs/security.md`](./docs/auth.md) - 다중 사용자 인증 및 OAuth2 처리 흐름
- [`docs/aop.md`](./docs/aop.md) - AOP 성능 측정 및 로깅 구조
- [`docs/api.md`](./docs/api.md) - API 구성 및 BaseApiClient 설계
- [`docs/screenshots.md`](./docs/screenshots.md) - 전체 기능별 UI 화면 모음
- [`docs/etc.md`]


## 💬 구현하며 마주한 기술적 도전

| 도전 과제                          | 해결 방법                                |
|--------------------------------|--------------------------------------|
| 공공 API에서 상세 관광데이터를 1건씩만 조회 가능  | 지연로딩 구조로 API호출 후 동기화                 |
| Member, Seller 나뉘어진 사용자 로그인 처리 | Spring Security Filter Chain 분리 적용   |
| 공통 데이터 반복 주입 문제                | `@ControllerAdvice` 기반 AOP로 모델 자동 주입 |
| 로그 일관성 부족                      | AOP 기반 로깅 체계 구축 (카테고리/성능/파라미터 기준)    |



## ✅ 담당 역할

- **전체 프로젝트 설계 및 구현 100% 주도**
- 공공 API 데이터 연동 / 파싱 / 저장 구조 설계
- 기차-버스 통합 화면 및 UI 흐름 구현
- Spring Security 다중 인증 체계 구성
- AOP 기반 성능 분석 및 전 계층 로깅 구축
- 비동기 데이터 처리 및 오류 대응 구조 설계
