<details> <summary># 🔐 DUGAZA Spring Security 시스템</summary>

> **"복잡한 보안을 간단하게, 강력한 인증을 유연하게"**

## 🎯 프로젝트 개요

DUGAZA는 여행 정보 통합 플랫폼으로, **다중 사용자 타입**과 **소셜 로그인**을 지원하는 고도화된 Spring Security 시스템을 구축했습니다.

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
<details>
<summary>### 1. 🎭 다중 Security Filter Chain</summary>

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
<details><summary>
### 2. 👤 통합 사용자 관리 (CustomUserDetails)</summary>

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
<details><summary>
### 3. 🔗 OAuth2 소셜 로그인 (카카오)</summary>

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
<details><summary>
### 4. 🎯 인증 핸들러</summary>

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
---

### ✅ **성과**
- **다중 사용자 타입**: Member/Seller 완벽 분리 관리
- **소셜 로그인**: 카카오 OAuth2 완전 통합
- **보안 강화**: CSRF, 세션 관리, Remember-Me
- **확장성**: 새로운 역할/권한 쉽게 추가 가능
- **유지보수성**: 명확한 책임 분리로 코드 관리 용이

---
</details>

<details>
   <summary> # 🌐 DUGAZA API 시스템</summary>

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

<details>
<summary>### 1. 🎭 다중 HTTP 클라이언트 아키텍처</summary>
</details>
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

<details>
    <summary>
### 2. 🎯 전문화된 API 클라이언트
    </summary>

#### **관광청 API 클라이언트**
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

### 🏛️ 관광청 API
| API | 설명 | 엔드포인트 |
|-----|------|------------|
| 지역 코드 | 전국 지역 코드 조회 | `/areaCode2` |
| 관광지 상세 | 관광지 상세 정보 | `/detailCommon1` |
| 카테고리 검색 | 카테고리별 관광지 검색 | `/searchCategory1` |
| 이벤트 정보 | 지역별 이벤트 정보 | `/searchFestival1` |
| 숙박 정보 | 지역별 숙박 정보 | `/searchStay1` |
| 음식점 정보 | 지역별 음식점 정보 | `/searchRestaurant1` |

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
