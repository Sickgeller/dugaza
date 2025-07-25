## 🎯 프로젝트 개요

DUGAZA는 여행 정보 통합 플랫폼으로, 사용자와 판매자로 크게 이루어져있으며 공공 API를 기반으로한 여행정보 제공 및 관련 상품 (렌터카, 숙소 판매등등) 에 관련된 서비스를 제공하는 통합 플랫폼입니다.

<h3>단순 구현 (CRUD)</h3>
<details>
    <summary>
    # 🚄🚌 DUGAZA 교통 시스템
    </summary>
## 🎯 개요

**기차(KTX, ITX, 무궁화호)**와 **고속버스** 정보를 통합하여 당일 어떤 노선이 얼마에 있는지를 보여주는 화면입니다.

### 🌟 핵심 특징
- **실시간 교통 정보**: 기차와 고속버스 실시간 조회
- **통합 교통 서비스**: 하나의 플랫폼에서 모든 교통수단 정보 제공
- **API 기반 데이터**: 공공 교통 API를 활용한 정확한 정보
- **허브역 기반 검색**: 하위역 간 검색을 위한 허브역 중계 시스템

---

## 🏗️ 아키텍처 구조

### 🚄 **기차 허브역 시스템 - 핵심 **
기차 노선 검색 시스템의 가장 소개할만한 특징은 **허브역 기반 검색 시스템**입니다. 
국토교통부(Tago) 열차정보 API에서는 하위역 간 직접 검색(여수 -> 의정부 등 중간에 "핵심역"을 지나고 "핵심역"의 부속 역들을 직접 검색하는것)을 지원하지 않기 때문에, 
17개의 핵심 허브역을 지정하여 **하위역 → 허브역 → 허브역 → 하위역** 형태의 모든 경로를 미리 계산하여 DB에 저장하였습니다.


### 📊 전체 교통 시스템 구조도
```
┌─────────────────────────────────────────────────────────────┐
│                    DUGAZA Transportation System              │
├─────────────────────────────────────────────────────────────┤
│  🎯 Controller Layer                                       │
│  ├── TransportationController (통합 컨트롤러)              │
│  ├── 기차/버스 페이지 라우팅                                │
│  └── REST API 엔드포인트                                    │
├─────────────────────────────────────────────────────────────┤
│  🔌 API Client Layer                                       │
│  ├── TrainApiClient (기차 API)                             │
│  ├── ExpressBusApiClient (고속버스 API)                    │
│  └── BaseApiClient (공통 API 인터페이스)                   │
├─────────────────────────────────────────────────────────────┤
│  📊 Service Layer                                          │
│  ├── TrainService (기차 서비스)                            │
│  ├── ExpressBusService (고속버스 서비스)                   │
│  └── 데이터 처리 및 비즈니스 로직                           │
├─────────────────────────────────────────────────────────────┤
│  🗄️ Data Access Layer                                     │
│  ├── TrainMapper (기차 데이터)                             │
│  ├── ExpressBusTerminalApiMapper (버스 터미널)             │
│  └── TrainCityApiMapper (도시 정보)                        │
├─────────────────────────────────────────────────────────────┤
│  🌐 External API                                           │
│  ├── 기차 API (공공데이터포털)                             │
│  ├── 고속버스 API (공공데이터포털)                         │
│  └── 실시간 교통 정보                                       │
└─────────────────────────────────────────────────────────────┘
```

---

## 🔧 핵심 컴포넌트

<details>
<summary>🎯 통합 교통 컨트롤러</summary>

#### **TransportationController - 교통 서비스 통합 관리**
```java
@Slf4j
@Controller
@RequestMapping("/transportation")
@RequiredArgsConstructor
public class TransportationController {

    private final ExpressBusApiClient expressBusApiClient;
    private final ExpressBusTerminalApiMapper expressBusTerminalApiMapper;
    private final TrainCityApiMapper trainCityApiMapper;
    private final TrainService trainService;

    // 메인 교통 페이지
    @GetMapping("")
    public String transportationMain() {
        return "views/transportation/transportation";
    }
    
    // 기차 조회 페이지
    @GetMapping("/train")
    public String trainForm() {
        return "views/transportation/train";
    }
    
    // 버스 조회 페이지
    @GetMapping("/bus")
    public String busForm() { 
        return "views/transportation/bus";
    }
}
```

#### **기차 관련 API 엔드포인트**
```java
// 기차 도시 목록 조회
@GetMapping("/train/cities")
@ResponseBody
public ResponseEntity<List<TrainCityVO>> getTrainCities() {
    try {
        List<TrainCityVO> cities = trainService.getAllCities();
        return ResponseEntity.ok(cities);
    } catch (Exception e) {
        log.error("기차 도시 목록 조회 중 오류 발생", e);
        return ResponseEntity.internalServerError().build();
    }
}

// 도시별 기차역 목록 조회
@GetMapping("/train/stations/{cityCode}")
@ResponseBody
public ResponseEntity<List<TrainStationVO>> getTrainStationsByCity(@PathVariable Integer cityCode) {
    try {
        List<TrainStationVO> stations = trainService.getStationsByCity(cityCode);
        return ResponseEntity.ok(stations);
    } catch (Exception e) {
        log.error("도시별 기차역 목록 조회 중 오류 발생", e);
        return ResponseEntity.internalServerError().build();
    }
}

// 기차 노선 검색
@PostMapping("/train/search")
@ResponseBody
public ResponseEntity<List<TrainRouteVO>> searchTrainRoutes(
        @RequestParam(name = "depPlaceName") String depPlaceName,
        @RequestParam(name = "arrPlaceName") String arrPlaceName,
        @RequestParam(name = "depPlandTime") String depPlandTime) {
    try {
        Map<String, Object> params = new HashMap<>();
        params.put("depPlaceName", depPlaceName);
        params.put("arrPlaceName", arrPlaceName);
        params.put("depPlandTime", depPlandTime);
        
        List<TrainRouteVO> routes = trainService.searchRoutes(params);
        return ResponseEntity.ok(routes);
    } catch (Exception e) {
        log.error("기차 노선 검색 중 오류 발생", e);
        return ResponseEntity.internalServerError().build();
    }
}
```

#### **고속버스 관련 API 엔드포인트**
```java
// 고속버스 도시 목록 조회
@GetMapping("/bus/cities")
@ResponseBody
public ResponseEntity<List<TrainCityApiDto>> getCities() {
    try {
        List<TrainCityApiDto> cities = trainCityApiMapper.getAllCityDto();
        return ResponseEntity.ok(cities);
    } catch (Exception e) {
        log.error("고속버스 도시 목록 조회 중 오류 발생", e);
        return ResponseEntity.internalServerError().build();
    }
}

// 고속버스 터미널 목록 조회
@GetMapping("/bus/terminals")
@ResponseBody
public ResponseEntity<List<ExpressBusTerminalApiDto>> getTerminals() {
    try {
        List<ExpressBusTerminalApiDto> terminals = expressBusTerminalApiMapper.selectAll();
        return ResponseEntity.ok(terminals);
    } catch (Exception e) {
        log.error("고속버스 터미널 목록 조회 중 오류 발생", e);
        return ResponseEntity.internalServerError().build();
    }
}

// 고속버스 경로 검색
@PostMapping("/bus/search")
@ResponseBody
public ResponseEntity<List<ExpressBusRouteApiDto>> searchBusRoutes(
        @RequestParam(name = "depTerminalId") String depTerminalId,
        @RequestParam(name = "arrTerminalId") String arrTerminalId,
        @RequestParam(name = "depPlandTime") String depPlandTime) {
    try {
        List<ExpressBusRouteApiDto> routes = expressBusApiClient.searchRoutes(
            depTerminalId, arrTerminalId, depPlandTime);
        return ResponseEntity.ok(routes);
    } catch (Exception e) {
        log.error("고속버스 경로 검색 중 오류 발생", e);
        return ResponseEntity.internalServerError().build();
    }
}
```
</details>

<details>
<summary>🚄 기차 노선 조회</summary>

#### **TrainApiClient - 기차 API 통합 관리**
```java
@Component
@Slf4j
@RequiredArgsConstructor
public class TrainApiClient {

    private final BaseApiClient baseApiClient;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final DateTimeFormatter PARSING_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    ...
    
    /**
     * 기차 노선 조회
     */
    @LogExecutionTime(category = "TrainRoute")
    public List<TrainRouteApiDto> getTrainRouteData(String depNodeId, String arrNodeId) {
        URI uri = baseApiClient.makeTrainUri("/getStrtpntAlocFndTrainInfo",
                "depPlaceId", depNodeId,
                "arrPlaceId", arrNodeId,
                "depPlandTime", PARSING_FORMATTER.format(LocalDateTime.now()));
        return baseApiClient.callApiManyTimes(uri, this::createTrainRouteDto);
    }
}
```


<img width="776" height="850" alt="기차 노선 검색 1" src="https://github.com/user-attachments/assets/f21507ac-f393-433d-b9da-865f5ebfb722" /><br>
<img width="1610" height="122" alt="image" src="https://github.com/user-attachments/assets/478d446a-7999-4360-bb20-c4fc2412974f" /><br>
비동기방식으로 도시목록 로딩후 도시선택 이후 비동기 방식으로 해당 지역내의 역을 로딩

<img width="838" height="850" alt="image" src="https://github.com/user-attachments/assets/d4061db0-2728-4563-965f-1fb7b5ef2336" /><br>
출발역과 도착역 모두선택후 검색 클릭시 결과에 맞는 역 결과 등장
<img width="751" height="850" alt="image" src="https://github.com/user-attachments/assets/1bd08229-431d-4482-b4fc-b1d46e4b3392" /><br>

</details>


<details><summary> 🚌 고속버스 노선 검색</summary>

<img width="802" height="819" alt="image" src="https://github.com/user-attachments/assets/533f3710-5539-4d70-b953-f401048b554a" />
<img width="1602" height="93" alt="image" src="https://github.com/user-attachments/assets/6698b276-d16a-496b-8a68-d4af8726c7e8" /><br>
비동기 방식으로 페이지 로드시 지역을 선택
<img width="1682" height="60" alt="image" src="https://github.com/user-attachments/assets/d5ab0b9f-c397-4395-8e6c-1867b9ce7eea" /><br>
비동기 방식으로 도시 지역 선택시 지역에 해당하는 터미널 출력 이후 터미널 선택후 검색시

<img width="578" height="377" alt="image" src="https://github.com/user-attachments/assets/0f8e045e-e033-4e3d-9b12-2ec53814de5a" />

해당 파라미터값에 맞는 결과가 나오게됨. 고속버스는 API에서 노선검색이 편해서 더 쉬웠음

</details>

---



---

## 📊 API 목록

### 🚄 기차 API
| API | 설명 | 엔드포인트 | 메서드 |
|-----|------|------------|--------|
| 기차 종류 | 기차 종류 목록 (KTX, ITX, 무궁화호) | `/getVhcleKndList` | GET |
| 지역 코드 | 기차 지역 코드 목록 | `/getCtyCodeList` | GET |
| 역 정보 | 도시별 기차역 정보 | `/getCtyAcctoTrainSttnList` | GET |
| 노선 조회 | 기차 노선 정보 | `/getStrtpntAlocFndTrainInfo` | POST |

### 🚌 고속버스 API
| API | 설명 | 엔드포인트 | 메서드 |
|-----|------|------------|--------|
| 도시 목록 | 고속버스 도시 코드 | `/getCtyCodeList` | GET |
| 터미널 목록 | 고속버스 터미널 정보 | `/getExpBusTrminlList` | GET |
| 등급 목록 | 고속버스 등급 정보 | `/getExpBusGradList` | GET |
| 노선 조회 | 고속버스 노선 정보 | `/getStrtpntAlocFndExpbusInfo` | POST |

### 🎯 내부 API 엔드포인트
| API | 설명 | 엔드포인트 | 메서드 |
|-----|------|------------|--------|
| 교통 메인 | 교통 서비스 메인 페이지 | `/transportation` | GET |
| 기차 조회 | 기차 조회 페이지 | `/transportation/train` | GET |
| 버스 조회 | 고속버스 조회 페이지 | `/transportation/bus` | GET |
| 기차 도시 | 기차 도시 목록 | `/transportation/train/cities` | GET |
| 기차역 | 도시별 기차역 목록 | `/transportation/train/stations/{cityCode}` | GET |
| 기차 검색 | 기차 노선 검색 | `/transportation/train/search` | POST |
| 버스 도시 | 고속버스 도시 목록 | `/transportation/bus/cities` | GET |
| 버스 터미널 | 고속버스 터미널 목록 | `/transportation/bus/terminals` | GET |
| 버스 검색 | 고속버스 노선 검색 | `/transportation/bus/search` | POST |

---

## 🚀 사용 방법

### 1. **기차 조회 플로우**
```javascript
// 1. 도시 목록 로드
fetch('/transportation/train/cities')
    .then(response => response.json())
    .then(cities => {
        // 도시 선택 옵션 생성
        populateCitySelect(cities);
    });

// 2. 도시 선택 시 기차역 목록 로드
function onCityChange(cityCode) {
    fetch(`/transportation/train/stations/${cityCode}`)
        .then(response => response.json())
        .then(stations => {
            // 기차역 선택 옵션 생성
            populateStationSelect(stations);
        });
}

// 3. 기차 노선 검색
function searchTrainRoutes() {
    const formData = new FormData();
    formData.append('depPlaceName', departureStation);
    formData.append('arrPlaceName', arrivalStation);
    formData.append('depPlandTime', departureDate);
    
    fetch('/transportation/train/search', {
        method: 'POST',
        body: formData
    })
    .then(response => response.json())
    .then(routes => {
        // 검색 결과 표시
        displayTrainRoutes(routes);
    });
}
```

### 2. **고속버스 조회 플로우**
```javascript
// 1. 도시 목록 로드
fetch('/transportation/bus/cities')
    .then(response => response.json())
    .then(cities => {
        // 도시 선택 옵션 생성
        populateBusCitySelect(cities);
    });

// 2. 도시 선택 시 터미널 목록 로드
function onBusCityChange(cityCode) {
    fetch(`/transportation/bus/terminals/${cityCode}`)
        .then(response => response.json())
        .then(terminals => {
            // 터미널 선택 옵션 생성
            populateTerminalSelect(terminals);
        });
}

// 3. 고속버스 노선 검색
function searchBusRoutes() {
    const formData = new FormData();
    formData.append('depTerminalId', departureTerminal);
    formData.append('arrTerminalId', arrivalTerminal);
    formData.append('depPlandTime', departureDate);
    
    fetch('/transportation/bus/search', {
        method: 'POST',
        body: formData
    })
    .then(response => response.json())
    .then(routes => {
        // 검색 결과 표시
        displayBusRoutes(routes);
    });
}
```



---

## 🎨 UI/UX 특징

### 📱 **반응형 디자인**
- **모바일 최적화**: 터치 친화적 인터페이스
- **데스크톱 지원**: 넓은 화면에서의 최적화된 레이아웃
- **접근성**: 키보드 네비게이션 지원

### 🎯 **사용자 경험**
- **단계별 검색**: 도시 → 역/터미널 → 날짜 → 검색
- **실시간 피드백**: 로딩 상태 및 에러 메시지
- **직관적 아이콘**: Font Awesome 아이콘 활용
- **상태 표시**: 조회 가능/예약 가능 상태 뱃지

### 🎨 **시각적 요소**
- **카드 기반 레이아웃**: 정보의 명확한 구분
- **색상 코딩**: 교통수단별 구분 색상
- **애니메이션**: 부드러운 전환 효과
- **그리드 시스템**: 일관된 레이아웃

---

---

## ✅ **성과**
- **통합 교통 서비스**: 기차와 고속버스 정보를 하나의 플랫폼에서 제공
- **실시간 정보**: 공공 API를 통한 정확한 실시간 교통 정보
- **사용자 친화적**: 직관적이고 쉬운 검색 인터페이스
- **확장 가능한 구조**: 새로운 교통수단 쉽게 추가 가능
- **성능 최적화**: AOP 기반 실행 시간 모니터링 및 로깅

---


</details>

<h3>구현에 어려움이 있었던 부분들</h3>
<details>
<summary> 🔐 DUGAZA Spring Security 시스템</summary>

> **"복잡한 보안을 간단하게, 강력한 인증을 유연하게"**

## 🎯 개요

Spring Security를 활용하여 **다중 사용자 타입**과 **소셜 로그인**을 지원하는 고도화된 인증 / 인가 시스템을 구축했습니다.

### 🌟 핵심 특징
- **다중 사용자 타입**: Member(일반회원) + Seller(판매자) 분리 관리
- **소셜 로그인**: 카카오 OAuth2 완전 통합
- **다중 Security Filter Chain**: 웹과 API 보안 정책 분리
- **동적 권한 관리**: 역할 기반 접근 제어 (RBAC)
- **Remember-Me**: 7일간 자동 로그인

---

## 🏗️ 아키텍처 구조

### 📊 전체 보안 구조도
```
┌─────────────────────────────────────────────────────────────┐
│                    DUGAZA Security System                   │
├─────────────────────────────────────────────────────────────┤
│  🌐 Web Filter Chain (@Order(2))                           │
│  ├── Form Login (Member/Seller)                            │
│  ├── OAuth2 Login (Kakao)                                  │
│  ├── Remember-Me (7일)                                     │
│  └── Role-based Access Control                             │
├─────────────────────────────────────────────────────────────┤
│  🔌 API Filter Chain (@Order(1))                           │
│  ├── Basic Authentication                                  │
│  └── API-specific Authorization                            │
├─────────────────────────────────────────────────────────────┤
│  👥 User Management                                        │
│  ├── CustomUserDetails (Member + Seller)                   │
│  ├── CustomUserDetailsService                              │
│  └── CustomOAuth2UserService                               │
└─────────────────────────────────────────────────────────────┘
```

---

## 🔧 핵심 컴포넌트

---
<details>
<summary>🎭 다중 Security Filter Chain</summary>

#### **웹 애플리케이션용 Filter Chain**
```java
@Bean
@Order(2)
public SecurityFilterChain webSecurityFilterChain(HttpSecurity http) {
    return http
        .securityMatcher("/**")
        .authorizeHttpRequests(authorize -> authorize
            // 정적 리소스 허용
            .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
            // 공개 페이지
            .requestMatchers("/", "/member/login", "/member/register").permitAll()
            // 역할별 접근 제어
            .requestMatchers("/seller/**").hasAnyRole("SELLER", "CAR", "HOUSE")
            .requestMatchers("/admin/**").hasRole("ADMIN")
            // API는 별도 처리
            .requestMatchers("/api/**").denyAll()
            .anyRequest().authenticated()
        )
        .formLogin(form -> form
            .loginPage("/member/login")
            .successHandler(successHandler)
            .failureHandler(failureHandler)
        )
        .oauth2Login(oauth2 -> oauth2
            .userInfoEndpoint(userInfo -> 
                userInfo.userService(customOAuth2UserService)
            )
        )
        .rememberMe(remember -> remember
            .key(rememberMeKey)
            .tokenValiditySeconds(60 * 60 * 24 * 7) // 7일
        )
        .build();
}
```

#### **REST API용 Filter Chain**
```java
@Bean
@Order(1)
public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) {
    return http
        .securityMatcher("/api/**")
        .authorizeHttpRequests(authorize -> authorize
            .requestMatchers("/api/public/**").permitAll()
            .requestMatchers("/api/user/**").hasRole("USER")
            .requestMatchers("/api/seller/**").hasRole("SELLER")
            .requestMatchers("/api/admin/**").hasRole("ADMIN")
            .anyRequest().authenticated()
        )
        .httpBasic(basic -> {}) // API에 적합한 인증
        .build();
}
```
</details>
<details>
<summary>👤 통합 사용자 관리 (CustomUserDetails)</summary>

#### **다중 사용자 타입 지원**
```java
public class CustomUserDetails implements UserDetails {
    private final MemberVO member;
    private SellerVO seller;
    
    // 생성자 오버로딩으로 다양한 사용자 타입 지원
    public CustomUserDetails(MemberVO member) { ... }
    public CustomUserDetails(MemberVO member, SellerVO seller) { ... }
    public CustomUserDetails(SellerVO seller) { ... }
    
    // 동적 권한 부여
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        
        // Member 권한 (MEMBER, ADMIN)
        if (member != null) {
            UserRole userRole = UserRole.fromValue(member.getRole());
            authorities.add(new SimpleGrantedAuthority(userRole.getRoleCode()));
        }
        
        // Seller 권한 (SELLER, CAR_SELLER, HOUSE_SELLER)
        if (seller != null) {
            SellerRole sellerRole = SellerRole.valueOf(seller.getRole());
            authorities.add(new SimpleGrantedAuthority("ROLE_" + sellerRole.getValue()));
        }
        
        return authorities;
    }
}
```
</details>
<details>
<summary>🔗 OAuth2 소셜 로그인 (카카오)</summary>

#### **카카오 로그인 플로우**
```java
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    
    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) {
        OAuth2User user = super.loadUser(request);
        String registrationId = request.getClientRegistration().getRegistrationId();
        
        if ("kakao".equals(registrationId)) {
            // 카카오 API 응답 처리
            Long providerId = Long.valueOf(user.getAttribute("id").toString());
            
            // 1. 기존 회원 조회
            MemberVO member = findByKakaoId(providerId);
            
            if (member == null) {
                // 2. 신규 회원 → 통합 페이지로 리다이렉트
                throw new BadCredentialsException("NEW_ACCOUNT_REQUIRED:" + providerId);
            }
            
            if ("KAKAO".equals(member.getStatus())) {
                // 3. 미완료 카카오 회원 → 통합 페이지로 리다이렉트
                throw new BadCredentialsException("INTEGRATION_REQUIRED:" + providerId);
            }
            
            return new CustomOAuth2User(member, user.getAttributes());
        }
    }
}
```

#### **카카오 계정 통합 플로우**
```
1. 카카오 로그인 시도
   ↓
2. 기존 회원인가?
   ├─ Yes → 로그인 성공
   └─ No → 3번으로
   ↓
3. 신규 회원인가?
   ├─ Yes → 통합 페이지로 이동 (신규 가입)
   └─ No → 4번으로
   ↓
4. 미완료 카카오 회원인가?
   ├─ Yes → 통합 페이지로 이동 (기존 계정 연동)
   └─ No → 로그인 성공
```

</details>


<details>
<summary>🎯 인증 핸들러</summary>

#### **성공 핸들러 (역할별 리다이렉트)**
```java
@Component
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, 
                                      HttpServletResponse response, 
                                      Authentication authentication) {
        
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        
        // 사용자 타입 검증
        if (!validateUserTypeAndRole(requestedUserType, userDetails)) {
            clearAuthenticationAndSession(request, response, authentication);
            redirectToLoginPage(requestedUserType);
            return;
        }
        
        // 역할별 리다이렉트
        String targetUrl = determineTargetUrl(userDetails);
        setDefaultTargetUrl(targetUrl);
        
        super.onAuthenticationSuccess(request, response, authentication);
    }
    
    private String determineTargetUrl(CustomUserDetails userDetails) {
        if (userDetails.isSeller()) {
            return "/seller/dashboard";
        } else if (userDetails.hasRole("ROLE_ADMIN")) {
            return "/admin/dashboard";
        } else {
            return "/";
        }
    }
}
```

#### **실패 핸들러 (OAuth2 통합 처리)**
```java
@Component
public class CustomFailureHandler implements AuthenticationFailureHandler {
    
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, 
                                      HttpServletResponse response, 
                                      AuthenticationException exception) {
        
        // OAuth2 통합/가입 요청 처리
        if (exception.getMessage().startsWith("INTEGRATION_REQUIRED:")) {
            // 기존 회원 통합 페이지로 리다이렉트
            redirectToIntegrationPage(request, response, exception.getMessage());
        } else if (exception.getMessage().startsWith("NEW_ACCOUNT_REQUIRED:")) {
            // 신규 가입 페이지로 리다이렉트
            redirectToRegistrationPage(request, response, exception.getMessage());
        } else {
            // 일반 로그인 실패 처리
            redirectToLoginPageWithError(request, response, exception);
        }
    }
}
```
</details>
<hr>
</details>

<details>
<summary>🌐 DUGAZA API 시스템</summary>

> **"다양한 외부 API를 하나의 인터페이스로, 성능과 안정성을 모두 잡다"**

## 🎯 프로젝트 개요

DUGAZA는 **여행 정보 통합 플랫폼**으로, 공공 관광 API, 기차, 고속버스, 카카오 등 다양한 외부 API를 통합하여 사용자에게 풍부한 여행 정보를 제공합니다.

### 🌟 핵심 특징
- **Rest 클라이언트**: RestClient + WebClient 성능 비교 후 RestClient 선택
- **통합 API 인터페이스**: 모든 외부 API를 일관된 방식으로 호출
- **실시간 성능 모니터링**: AOP 기반 실행 시간 측정 및 로깅
- **비동기 로깅**: API 호출 이력 추적 및 분석
- **확장 가능한 구조**: 새로운 API 쉽게 추가 가능

---

## 🏗️ 아키텍처 구조

### 📊 전체 API 시스템 구조도
```
┌─────────────────────────────────────────────────────────────┐
│                    DUGAZA API System                        │
├─────────────────────────────────────────────────────────────┤
│  🔌 HTTP Client Layer                                      │
│  ├── RestClient (기본)                                     │
│  ├── WebClient (성능 비교용)                               │
│  └── Interface: BaseApiClient                              │
├─────────────────────────────────────────────────────────────┤
│  🎯 API Client Layer                                       │
│  ├── TourApiClient (관광청 API)                            │
│  ├── TrainApiClient (기차 API)                             │
│  ├── ExpressBusApiClient (고속버스 API)                    │
│  ├── KakaoApiClient (카카오 API)                           │
│  └── CategoryApiClient (카테고리 API)                      │
├─────────────────────────────────────────────────────────────┤
│  📊 Monitoring & Logging                                   │
│  ├── AOP LogExecutionTime                                  │
│  ├── ApiLogUtil (비동기 로깅)                              │
│  └── Performance Test Controller                           │
├─────────────────────────────────────────────────────────────┤
│  ⚙️ Configuration Layer                                    │
│  ├── ApiConfig (설정 관리)                                 │
│  ├── RestClientConfig                                      │
│  └── WebClientConfig                                       │
└─────────────────────────────────────────────────────────────┘
```

---

## 🔧 핵심 컴포넌트

---
<details>
<summary>🎭 다중 HTTP 클라이언트 아키텍처</summary>
#### **WebClient, RestClient, RestTemplate등등 구현방식을 선택할수있는 BaseApiClient 인터페이스**
```java
public interface BaseApiClient {
    /**
     * API 호출 메서드
     * @param uri API URI
     * @param dtoCreator DTO 생성 함수
     * @return API 응답 데이터 리스트
     */
    <T> List<T> callApi(URI uri, BiFunction<JsonNode, String, T> dtoCreator);

    /**
     * 여러 페이지의 API 호출 메서드
     * @param uri API URI
     * @param dtoCreator DTO 생성 함수
     * @return 모든 페이지의 API 응답 데이터 리스트
     */
    <T> List<T> callApiManyTimes(URI uri, BiFunction<JsonNode, String, T> dtoCreator);

    /**
     * 관광 API URI 생성
     * @param path API 경로
     * @param params 추가 파라미터들
     * @return 완성된 URI
     */
    URI makeTourUri(String path, String... params);

    /**
     * 기차 API URI 생성
     * @param path API 경로
     * @param params 추가 파라미터들
     * @return 완성된 URI
     */
    URI makeTrainUri(String path, String... params);

    /**
     * 고속버스 API URI 생성
     * @param path API 경로
     * @param params 추가 파라미터들
     * @return 완성된 URI
     */
    URI makeExpressBusUri(String path, String... params);
}
```

#### **RestClient 구현체 (기본)**
```java
@Slf4j
@Primary
@Component("restClientApiClient")
public class RestClientApiClient implements BaseApiClient {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final ApiConfig apiConfig;

    @Override
    @LogExecutionTime(category = "RestClientAPI")
    public <T> List<T> callApi(URI uri, BiFunction<JsonNode, String, T> dtoCreator) {
        LocalDateTime requestTime = LocalDateTime.now();
        ApiLogDto apiLog = null;

        // API 로그 생성
        if (apiLogUtil != null) {
            String apiName = extractApiName(uri.getPath());
            String requestParams = maskServiceKey(uri.getQuery());
            apiLog = apiLogUtil.createApiLog(apiName, uri.toString(), requestParams);
        }

        try {
            // RestClient로 API 호출
            String response = restClient.get()
                    .uri(uri)
                    .retrieve()
                    .body(String.class);

            long executionTime = ChronoUnit.MILLIS.between(requestTime, LocalDateTime.now());

            // 응답 유효성 검사
            if (!validateResponse(response)) {
                if (apiLogUtil != null && apiLog != null) {
                    apiLogUtil.updateFailLog(apiLog, "ERROR", "Invalid response", executionTime);
                }
                return new ArrayList<>();
            }

            // 응답 파싱
            List<T> result = parseApiResponse(response, dtoCreator);

            // 성공 로그 저장
            if (apiLogUtil != null && apiLog != null) {
                String responsePreview = response != null ?
                        (response.length() > 1000 ? response.substring(0, 1000) + "..." : response) : "null";
                apiLogUtil.updateSuccessLog(apiLog, responsePreview, result.size(), executionTime);
            }

            return result;
        } catch (Exception e) {
            log.error("RestClient API 호출 중 예외 발생: {}", e.getMessage(), e);
            if (apiLogUtil != null && apiLog != null) {
                long executionTime = ChronoUnit.MILLIS.between(requestTime, LocalDateTime.now());
                apiLogUtil.updateFailLog(apiLog, "EXCEPTION", e.getMessage(), executionTime);
            }
            return new ArrayList<>();
        }
    }
}
```

#### **WebClient 구현체 (성능 비교용)**
```java
@Slf4j
@Component("webClientApiClient")
public class WebClientBaseApiClient implements BaseApiClient {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final ApiConfig apiConfig;

    @Override
    @LogExecutionTime(category = "WebClientAPI")
    public <T> List<T> callApi(URI uri, BiFunction<JsonNode, String, T> dtoCreator) {
        // WebClient로 API 호출 (비동기 방식)
        String response = webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(String.class)
                .block(); // 동기적으로 처리

        // 나머지 로직은 RestClient와 동일
        return parseApiResponse(response, dtoCreator);
    }
}
```

</details>
<details>
<summary>🎯 전문화된 API 클라이언트</summary>

#### **공통 관광 데이터 API 클라이언트**
```java
@Slf4j
@Component
@RequiredArgsConstructor
public class TourApiClient {

    private final BaseApiClient baseApiClient;

    /**
     * 지역 코드 목록 조회
     */
    @LogExecutionTime(category = "TourData")
    public List<AreaCodeApiDto> getAreaCodeData() {
        URI uri = baseApiClient.makeTourUri("/areaCode2");
        return baseApiClient.callApi(uri, this::createAreaCodeDto);
    }

    /**
     * 관광지 상세 정보 조회
     */
    @LogExecutionTime(category = "TourData")
    public List<TouristAttractionApiDto> getTouristAttractionData(String areaCode, String contentTypeId) {
        URI uri = baseApiClient.makeTourUri("/detailCommon1", 
                "areaCode", areaCode, 
                "contentTypeId", contentTypeId);
        return baseApiClient.callApi(uri, this::createTouristAttractionDto);
    }

    /**
     * 카테고리별 관광지 검색
     */
    @LogExecutionTime(category = "TourData")
    public List<TouristAttractionApiDto> searchTouristAttractionByCategory(String areaCode, String contentTypeId) {
        URI uri = baseApiClient.makeTourUri("/searchCategory1", 
                "areaCode", areaCode, 
                "contentTypeId", contentTypeId);
        return baseApiClient.callApi(uri, this::createTouristAttractionDto);
    }
}
```

#### **기차 API 클라이언트**
```java
@Slf4j
@Component
@RequiredArgsConstructor
public class TrainApiClient {

    private final BaseApiClient baseApiClient;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    /**
     * 기차 종류 목록 조회
     */
    @LogExecutionTime(category = "TrainData")
    public List<TrainKindApiDto> getTrainKindData() {
        URI uri = baseApiClient.makeTrainUri("/getVhcleKndList");
        return baseApiClient.callApi(uri, this::createTrainKindDto);
    }

    /**
     * 기차 지역 코드 조회
     */
    @LogExecutionTime(category = "TrainData")
    public List<TrainCityApiDto> getTrainAreaData() {
        URI uri = baseApiClient.makeTrainUri("/getCtyCodeList");
        return baseApiClient.callApi(uri, this::createTrainCityDto);
    }

    /**
     * 기차 노선 조회
     */
    @LogExecutionTime(category = "TrainData")
    public List<TrainRouteApiDto> getTrainRouteData(String depPlaceId, String arrPlaceId, String depPlandTime) {
        URI uri = baseApiClient.makeTrainUri("/getStrtpntAlocFndTrainInfo", 
                "depPlaceId", depPlaceId,
                "arrPlaceId", arrPlaceId,
                "depPlandTime", depPlandTime);
        return baseApiClient.callApi(uri, this::createTrainRouteDto);
    }
}
```

#### **고속버스 API 클라이언트**
```java
@Slf4j
@Component
@RequiredArgsConstructor
public class ExpressBusApiClient {

    private final BaseApiClient baseApiClient;

    /**
     * 고속버스 터미널 목록 조회
     */
    @LogExecutionTime(category = "BusData")
    public List<ExpressBusTerminalApiDto> getTerminalData() {
        URI uri = baseApiClient.makeExpressBusUri("/getTerminalList");
        return baseApiClient.callApi(uri, this::createTerminalDto);
    }

    /**
     * 고속버스 노선 조회
     */
    @LogExecutionTime(category = "BusData")
    public List<ExpressBusRouteApiDto> getRouteData(String depTerminalId, String arrTerminalId, String depPlandTime) {
        URI uri = baseApiClient.makeExpressBusUri("/getRouteInfo", 
                "depTerminalId", depTerminalId,
                "arrTerminalId", arrTerminalId,
                "depPlandTime", depPlandTime);
        return baseApiClient.callApi(uri, this::createRouteDto);
    }
}
```
</details>
 

## 📊 API 목록

### 🏛️ 관광 데이터 API (공공 TourAPI)
| API | 설명 | 엔드포인트 | ContentTypeId |
|-----|------|------------|---------------|
| 지역 코드 | 전국 지역 코드 조회 | `/areaCode2` | - |
| 카테고리 코드 | 카테고리 코드 조회 | `/categoryCode2` | - |
| 관광지 검색 | 지역별 관광지 검색 | `/searchCategory1` | 12 |
| 관광지 상세 | 관광지 상세 정보 | `/detailCommon1` | 12 |
| 관광지 소개 | 관광지 소개 정보 | `/detailIntro2` | 12 |
| 숙박 검색 | 지역별 숙박 검색 | `/searchStay2` | 32 |
| 숙박 상세 | 숙박 상세 정보 | `/detailCommon1` | 32 |
| 숙박 소개 | 숙박 소개 정보 | `/detailIntro2` | 32 |
| 음식점 검색 | 지역별 음식점 검색 | `/searchRestaurant1` | 39 |
| 음식점 상세 | 음식점 상세 정보 | `/detailCommon1` | 39 |
| 음식점 소개 | 음식점 소개 정보 | `/detailIntro2` | 39 |
| 이벤트 검색 | 지역별 이벤트 검색 | `/searchFestival2` | 15 |
| 이벤트 상세 | 이벤트 상세 정보 | `/detailCommon1` | 15 |
| 이벤트 소개 | 이벤트 소개 정보 | `/detailIntro2` | 15 |
| 여행코스 소개 | 여행코스 소개 정보 | `/detailIntro2` | 25 |
| 레포츠 소개 | 레포츠 소개 정보 | `/detailIntro2` | 28 |
| 문화시설 소개 | 문화시설 소개 정보 | `/detailIntro2` | 14 |
| 쇼핑 소개 | 쇼핑 소개 정보 | `/detailIntro2` | 38 |

### 🚄 기차 API
| API | 설명 | 엔드포인트 |
|-----|------|------------|
| 기차 종류 | 기차 종류 목록 | `/getVhcleKndList` |
| 지역 코드 | 기차 지역 코드 | `/getCtyCodeList` |
| 역 정보 | 기차역 정보 | `/getCtyAcctoTrainSttnList` |
| 노선 조회 | 기차 노선 정보 | `/getStrtpntAlocFndTrainInfo` |

### 🚌 고속버스 API
| API | 설명 | 엔드포인트 |
|-----|------|------------|
| 터미널 목록 | 고속버스 터미널 | `/getTerminalList` |
| 노선 조회 | 고속버스 노선 | `/getRouteInfo` |
| 시간표 조회 | 고속버스 시간표 | `/getStrtpntAlocFndBusInfo` |

### 카카오 로그인 API


### ✅ **성과**
- **통합 인터페이스**: 모든 외부 API를 일관된 방식으로 호출
- **성능 최적화**: RestClient/WebClient 성능 비교 및 최적화
- **실시간 모니터링**: AOP 기반 실행 시간 측정 및 로깅
- **확장성**: 새로운 API 쉽게 추가 가능
- **안정성**: 비동기 로깅, 예외 처리, 타임아웃 설정
<hr>
</details>

<details>
<summary>🔍 DUGAZA AOP 시스템</summary>

## 🎯 개요

**Aspect-Oriented Programming (AOP)**를 활용하여 **로깅, 성능 모니터링, 전역 모델 관리**를 체계적으로 구현했습니다. 이를 통해 비즈니스 로직과 횡단 관심사를 효과적으로 분리하여 유지보수성과 가독성을 크게 향상시켰습니다.

### 🌟 핵심 특징
- **다층 로깅 시스템**: Controller, Service, Mapper, API Client별 세분화된 로깅
- **성능 모니터링**: 실행 시간 측정 및 분석
- **전역 모델 관리**: 인증된 사용자 정보 자동 주입
- **커스텀 어노테이션**: `@LogExecutionTime`으로 선택적 성능 측정
- **체계적인 포인트컷**: 계층별 정확한 메서드 타겟팅

---

## 🏗️ AOP 아키텍처 구조

### 📊 전체 AOP 시스템 구조도

```
┌─────────────────────────────────────────────────────────────┐
│                    DUGAZA AOP System                        │
├─────────────────────────────────────────────────────────────┤
│  🎯 Controller Layer AOP                                   │
│  ├── ControllerLoggingAspect                              │
│  ├── GlobalModelAdvice                                    │
│  └── Request/Response 로깅                                │
├─────────────────────────────────────────────────────────────┤
│  🔧 Service Layer AOP                                     │
│  ├── ServiceLoggingAspect                                 │
│  ├── @LogExecutionTime                                    │
│  └── Business Logic 로깅                                  │
├─────────────────────────────────────────────────────────────┤
│  📊 Data Access Layer AOP                                 │
│  ├── MapperLoggingAspect                                  │
│  ├── SQL 실행 로깅                                        │
│  └── Performance 모니터링                                 │
├─────────────────────────────────────────────────────────────┤
│  🌐 API Client Layer AOP                                  │
│  ├── LoggingAspect                                        │
│  ├── External API 로깅                                    │
│  └── Response 분석                                        │
└─────────────────────────────────────────────────────────────┘
```

---

## 🔧 핵심 컴포넌트

---
<details>
<summary>🎯 Controller Layer AOP</summary>

#### **ControllerLoggingAspect - 웹 요청 로깅**
```java
@Aspect
@Component
@Slf4j
public class ControllerLoggingAspect {

    /**
     * 모든 컨트롤러 메서드에 대한 포인트컷
     */
    @Pointcut("execution(* kr.spring..*.controller..*Controller.*(..))")
    private void allControllerMethods() {}
    
    @Pointcut("@within(org.springframework.web.bind.annotation.RestController)")
    private void allRestControllerMethods() {}
    
    @Pointcut("@within(org.springframework.stereotype.Controller)")
    private void allControllerAnnotatedMethods() {}
    
    @Pointcut("allControllerMethods() || allRestControllerMethods() || allControllerAnnotatedMethods()")
    private void allWebControllerMethods() {}

    /**
     * 모든 컨트롤러 메서드 실행 전후 로깅
     */
    @Around("allWebControllerMethods()")
    public Object logControllerMethodExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String category = "[" + className.replace("Controller", "") + "]";
        
        // REST API인지 확인
        boolean isRestController = joinPoint.getTarget().getClass()
                .isAnnotationPresent(org.springframework.web.bind.annotation.RestController.class);
        
        // 요청 정보 가져오기
        HttpServletRequest request = null;
        String requestURI = "";
        String httpMethod = "";
        
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                request = attributes.getRequest();
                requestURI = request.getRequestURI();
                httpMethod = request.getMethod();
            }
        } catch (Exception e) {
            log.debug("요청 정보를 가져오는 데 실패했습니다: {}", e.getMessage());
        }
        
        // 메서드 파라미터 로깅
        Object[] args = joinPoint.getArgs();
        String params = Arrays.stream(args)
                .map(arg -> arg != null ? arg.toString() : "null")
                .collect(Collectors.joining(", "));
        
        String controllerType = isRestController ? "REST API" : "WEB";
        log.info("{} [{}] 요청 시작 - URI: {} [{}], 메서드: {}, 파라미터: [{}]", 
                category, controllerType, requestURI, httpMethod, methodName, params);
        
        LocalDateTime startTime = LocalDateTime.now();
        Object result;
        
        try {
            result = joinPoint.proceed();
            long executionTime = ChronoUnit.MILLIS.between(startTime, LocalDateTime.now());
            log.info("{} [{}] 요청 완료 - URI: {} [{}], 메서드: {}, 실행 시간: {}ms", 
                    category, controllerType, requestURI, httpMethod, methodName, executionTime);
            
            return result;
        } catch (Exception e) {
            long executionTime = ChronoUnit.MILLIS.between(startTime, LocalDateTime.now());
            log.error("{} [{}] 요청 오류 - URI: {} [{}], 메서드: {}, 실행 시간: {}ms, 오류: {}", 
                    category, controllerType, requestURI, httpMethod, methodName, executionTime, e.getMessage(), e);
            throw e;
        }
    }
}
```

#### **GlobalModelAdvice - 전역 모델 관리**
```java
@ControllerAdvice
public class GlobalModelAdvice {
    
    @ModelAttribute
    public void addModelMemberAndSeller(
            Model model,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        if(userDetails != null) {
            if(userDetails.getSeller() != null) {
                model.addAttribute("seller", userDetails.getSeller());
            }
            if(userDetails.getMember() != null) {
                model.addAttribute("member", userDetails.getMember());
            }
        }
    }
}
```

</details>
<details>
<summary>🔧 Service Layer AOP</summary>

#### **ServiceLoggingAspect - 서비스 로직 로깅**
```java
@Aspect
@Component
@Slf4j
public class ServiceLoggingAspect {

    /**
     * 모든 서비스 메서드에 대한 포인트컷
     */
    @Pointcut("execution(* kr.spring..*.service..*Service*.*(..))")
    private void allServiceMethods() {}

    /**
     * @LogExecutionTime 어노테이션이 없는 메서드에 대한 포인트컷
     */
    @Pointcut("allServiceMethods() && !@annotation(kr.spring.aop.LogExecutionTime)")
    private void nonAnnotatedServiceMethods() {}

    /**
     * 모든 서비스 메서드 실행 전후 로깅
     */
    @Around("nonAnnotatedServiceMethods()")
    public Object logServiceMethodExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String category = "[" + className.replace("ServiceImpl", "").replace("Service", "") + "]";
        
        // 메서드 파라미터 로깅
        Object[] args = joinPoint.getArgs();
        String params = Arrays.stream(args)
                .map(arg -> arg != null ? arg.toString() : "null")
                .collect(Collectors.joining(", "));
        
        log.debug("{} 서비스 시작 - 메서드: {}, 파라미터: [{}]", 
                category, methodName, params);
        
        LocalDateTime startTime = LocalDateTime.now();
        Object result;
        
        try {
            result = joinPoint.proceed();
            long executionTime = ChronoUnit.MILLIS.between(startTime, LocalDateTime.now());
            
            // 결과 요약
            String resultSummary = summarizeResult(result);
            log.debug("{} 서비스 완료 - 메서드: {}, 실행 시간: {}ms, 결과: {}", 
                    category, methodName, executionTime, resultSummary);
            
            return result;
        } catch (Exception e) {
            long executionTime = ChronoUnit.MILLIS.between(startTime, LocalDateTime.now());
            log.error("{} 서비스 오류 - 메서드: {}, 실행 시간: {}ms, 오류: {}", 
                    category, methodName, executionTime, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * 결과를 요약하여 문자열로 반환
     */
    private String summarizeResult(Object result) {
        if (result == null) {
            return "null";
        }
        
        if (result instanceof List<?>) {
            List<?> list = (List<?>) result;
            return String.format("List[%d items]", list.size());
        }
        
        if (result instanceof Map<?, ?>) {
            Map<?, ?> map = (Map<?, ?>) result;
            return String.format("Map[%d entries]", map.size());
        }
        
        return result.toString();
    }
}
```

</details>
<details>
<summary>📊 Data Access Layer AOP</summary>

#### **MapperLoggingAspect - SQL 실행 로깅**
```java
@Aspect
@Component
@Slf4j
public class MapperLoggingAspect {

    /**
     * 모든 Mapper 메서드에 대한 포인트컷
     */
    @Pointcut("execution(* kr.spring..*.mapper.*Mapper.*(..))")
    private void allMapperMethods() {}

    /**
     * Mapper 메서드 실행 전후 로깅
     */
    @Around("allMapperMethods()")
    public Object logMapperMethodExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        // 로그 레벨이 TRACE인 경우에만 상세 로깅
        if (!log.isTraceEnabled()) {
            return joinPoint.proceed();
        }
        
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String category = "[" + className.replace("Mapper", "") + "]";
        
        // 메서드 파라미터 로깅
        Object[] args = joinPoint.getArgs();
        String params = Arrays.stream(args)
                .map(arg -> arg != null ? arg.toString() : "null")
                .collect(Collectors.joining(", "));
        
        log.trace("{} SQL 실행 시작 - 메서드: {}, 파라미터: [{}]", 
                category, methodName, params);
        
        LocalDateTime startTime = LocalDateTime.now();
        Object result;
        
        try {
            result = joinPoint.proceed();
            long executionTime = ChronoUnit.MILLIS.between(startTime, LocalDateTime.now());
            
            // 결과 요약
            String resultSummary = result != null ? result.toString() : "null";
            log.trace("{} SQL 실행 완료 - 메서드: {}, 실행 시간: {}ms, 결과: {}", 
                    category, methodName, executionTime, resultSummary);
            
            return result;
        } catch (Exception e) {
            long executionTime = ChronoUnit.MILLIS.between(startTime, LocalDateTime.now());
            log.error("{} SQL 실행 오류 - 메서드: {}, 실행 시간: {}ms, 오류: {}", 
                    category, methodName, executionTime, e.getMessage(), e);
            throw e;
        }
    }
}
```

</details>
<details>
<summary>🌐 API Client Layer AOP</summary>

#### **LoggingAspect - API 호출 로깅**
```java
@Aspect
@Component
@Slf4j
public class LoggingAspect {

    /**
     * API 클라이언트 메서드에 대한 포인트컷
     */
    @Pointcut("execution(* kr.spring.api.client.*ApiClient.*(..))")
    private void apiClientMethods() {}

    /**
     * @LogExecutionTime 어노테이션이 붙은 메서드에 대한 포인트컷
     */
    @Pointcut("@annotation(kr.spring.aop.LogExecutionTime)")
    private void logExecutionTimeAnnotation() {}

    /**
     * API 클라이언트 메서드 실행 전후 로깅
     */
    @Around("apiClientMethods()")
    public Object logApiClientMethodExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String category = "[" + className.replace("ApiClient", "") + "]";
        
        // 메서드 파라미터 로깅
        Object[] args = joinPoint.getArgs();
        log.info("{} API 호출 시작 - 메서드: {}, 파라미터: {}", 
                category, methodName, Arrays.toString(args));
        
        LocalDateTime startTime = LocalDateTime.now();
        Object result;
        
        try {
            result = joinPoint.proceed();
            long executionTime = ChronoUnit.MILLIS.between(startTime, LocalDateTime.now());
            
            // 결과 크기 확인
            int resultSize = getResultSize(result);
            log.info("{} API 호출 완료 - 메서드: {}, 실행 시간: {}ms, 결과 크기: {}", 
                    category, methodName, executionTime, resultSize);
            
            return result;
        } catch (Exception e) {
            log.error("{} API 호출 오류 - 메서드: {}, 오류: {}", 
                    category, methodName, e.getMessage());
            throw e;
        }
    }

    /**
     * @LogExecutionTime 어노테이션이 붙은 메서드 실행 시간 로깅
     */
    @Around("logExecutionTimeAnnotation()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        LogExecutionTime annotation = method.getAnnotation(LogExecutionTime.class);
        
        String category = annotation.category();
        if (category.isEmpty()) {
            category = "[" + joinPoint.getTarget().getClass().getSimpleName() + "]";
        } else {
            category = "[" + category + "]";
        }
        
        String methodName = method.getName();
        log.info("{} 실행 시작 - 메서드: {}", category, methodName);
        
        LocalDateTime startTime = LocalDateTime.now();
        Object result;
        
        try {
            result = joinPoint.proceed();
            long executionTime = ChronoUnit.MILLIS.between(startTime, LocalDateTime.now());
            log.info("{} 실행 완료 - 메서드: {}, 실행 시간: {}ms", 
                    category, methodName, executionTime);
            
            return result;
        } catch (Exception e) {
            log.error("{} 실행 오류 - 메서드: {}, 오류: {}", 
                    category, methodName, e.getMessage(), e);
            throw e;
        }
    }
}
```

</details>
<details>
<summary>🏷️ 커스텀 어노테이션</summary>

#### **LogExecutionTime - 선택적 성능 측정**
```java
/**
 * 메서드 실행 시간을 로깅하기 위한 어노테이션
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogExecutionTime {
    /**
     * 로그에 표시할 카테고리
     */
    String category() default "";
}
```

---

## 🚀 사용 방법

### 1. **기본 로깅 (자동 적용)**
```java
@RestController
@RequestMapping("/api/tour")
public class TourController {

    private final TourApiClient tourApiClient;

    // 자동으로 ControllerLoggingAspect가 로깅 수행
    @GetMapping("/areas")
    public ResponseEntity<List<AreaCodeApiDto>> getAreas() {
        List<AreaCodeApiDto> areas = tourApiClient.getAreaCodeData();
        return ResponseEntity.ok(areas);
    }
}
```

### 2. **선택적 성능 측정**
```java
@Service
public class TourServiceImpl implements TourService {

    // @LogExecutionTime으로 선택적 성능 측정
    @LogExecutionTime(category = "TourData")
    public List<AreaCodeApiDto> getAreaCodeData() {
        URI uri = baseApiClient.makeTourUri("/areaCode2");
        return baseApiClient.callApi(uri, this::createAreaCodeDto);
    }

    // 일반 메서드는 ServiceLoggingAspect가 자동 로깅
    public void processTourData(List<AreaCodeApiDto> data) {
        // 비즈니스 로직
    }
}
```

### 3. **전역 모델 자동 주입**
```java
@ControllerAdvice
public class GlobalModelAdvice {
    @ModelAttribute
    public void addModelMemberAndSeller(
            Model model,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        if(userDetails != null) {
            if(userDetails.getSeller() != null) {
                model.addAttribute("seller", userDetails.getSeller());
            }
            if(userDetails.getMember() != null) {
                model.addAttribute("member", userDetails.getMember());
            }
        }
    }
} 
```
```html
<!-- Thymeleaf 템플릿에서 자동으로 사용 가능 -->
<div th:if="${member != null}">
    <p>안녕하세요, <span th:text="${member.name}">사용자</span>님!</p>
</div>

<div th:if="${seller != null}">
    <p>판매자: <span th:text="${seller.businessName}">업체명</span></p>
</div>
```

---

## 📊 로깅 출력 예시

### 🔍 **Controller 로깅**
```
[Tour] [REST API] 요청 시작 - URI: /api/tour/areas [GET], 메서드: getAreas, 파라미터: []
[Tour] [REST API] 요청 완료 - URI: /api/tour/areas [GET], 메서드: getAreas, 실행 시간: 245ms
```

### 🔧 **Service 로깅**
```
[Tour] 서비스 시작 - 메서드: getAreaCodeData, 파라미터: []
[Tour] 서비스 완료 - 메서드: getAreaCodeData, 실행 시간: 180ms, 결과: List[17 items]
```

### 📊 **Mapper 로깅 (TRACE 레벨)**
```
[Tour] SQL 실행 시작 - 메서드: selectAreaCodes, 파라미터: []
[Tour] SQL 실행 완료 - 메서드: selectAreaCodes, 실행 시간: 15ms, 결과: List[17 items]
```

### 🌐 **API Client 로깅**
```
[Tour] API 호출 시작 - 메서드: getAreaCodeData, 파라미터: []
[Tour] API 호출 완료 - 메서드: getAreaCodeData, 실행 시간: 165ms, 결과 크기: 17
```

### ⏱️ **성능 측정 로깅**
```
[TourData] 실행 시작 - 메서드: getAreaCodeData
[TourData] 실행 완료 - 메서드: getAreaCodeData, 실행 시간: 180ms
```

---

## 🎯 AOP의 강력한 분리 기능

### 🔄 **관심사 분리 (Separation of Concerns)**
```java
// 비즈니스 로직 (핵심 관심사)
@Service
public class TourServiceImpl {
    public List<AreaCodeApiDto> getAreaCodeData() {
        // 순수한 비즈니스 로직만 작성
        URI uri = baseApiClient.makeTourUri("/areaCode2");
        return baseApiClient.callApi(uri, this::createAreaCodeDto);
    }
}

// 로깅 로직 (횡단 관심사) - AOP로 자동 처리
@Aspect
@Component
public class ServiceLoggingAspect {
    @Around("execution(* kr.spring..*.service..*Service*.*(..))")
    public Object logServiceMethodExecution(ProceedingJoinPoint joinPoint) {
        // 로깅 로직이 비즈니스 로직과 완전히 분리됨
    }
}
```

### 🎨 **코드 재사용성**
```java
// 하나의 어노테이션으로 모든 계층에서 성능 측정 가능
@LogExecutionTime(category = "TourData")
public List<AreaCodeApiDto> getAreaCodeData() { ... }

@LogExecutionTime(category = "UserData")
public UserVO getUserInfo(Long userId) { ... }

@LogExecutionTime(category = "PaymentData")
public PaymentResult processPayment(PaymentRequest request) { ... }
```

### 🛡️ **유지보수성 향상**
```java
// 로깅 정책 변경 시 AOP 클래스만 수정하면 전체 시스템에 적용
@Around("allControllerMethods()")
public Object logControllerMethodExecution(ProceedingJoinPoint joinPoint) {
    // 로깅 형식 변경, 추가 정보 수집 등
    // 비즈니스 로직은 전혀 건드리지 않음
}
```

---

## 📈 로깅의 중요성

### 🔍 **문제 진단 및 디버깅**

```
// 실행 흐름 추적
[Tour] [REST API] 요청 시작 - URI: /api/tour/areas [GET]
[Tour] 서비스 시작 - 메서드: getAreaCodeData
[Tour] API 호출 시작 - 메서드: getAreaCodeData
[Tour] API 호출 완료 - 메서드: getAreaCodeData, 실행 시간: 165ms
[Tour] 서비스 완료 - 메서드: getAreaCodeData, 실행 시간: 180ms
[Tour] [REST API] 요청 완료 - URI: /api/tour/areas [GET], 실행 시간: 245ms
```

### 📊 **성능 모니터링**

```
// 성능 병목 지점 식별
[TourData] 실행 완료 - 메서드: getAreaCodeData, 실행 시간: 180ms
[UserData] 실행 완료 - 메서드: getUserInfo, 실행 시간: 45ms
[PaymentData] 실행 완료 - 메서드: processPayment, 실행 시간: 1200ms  // ⚠️ 성능 이슈
```

### 🚨 **오류 추적**

```
// 오류 발생 지점 및 원인 파악
[Tour] API 호출 오류 - 메서드: getAreaCodeData, 오류: Connection timeout
[Tour] 서비스 오류 - 메서드: getAreaCodeData, 실행 시간: 5000ms, 오류: Connection timeout
[Tour] [REST API] 요청 오류 - URI: /api/tour/areas [GET], 실행 시간: 5020ms, 오류: Connection timeout
```


---

### ✅ **성과**
- **관심사 분리**: 비즈니스 로직과 횡단 관심사 완벽 분리
- **체계적 로깅**: 계층별 세분화된 로깅 시스템
- **성능 모니터링**: 실시간 실행 시간 측정 및 분석
- **코드 재사용성**: 커스텀 어노테이션으로 선택적 기능 적용
- **유지보수성**: 로깅 정책 변경 시 AOP만 수정하면 전체 적용

</details>
