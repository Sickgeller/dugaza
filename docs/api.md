# 🌐 DUGAZA API 시스템

> **"다양한 외부 API를 하나의 인터페이스로, 성능과 안정성을 모두 잡다"**

## 🎯 프로젝트 개요

DUGAZA는 **여행 정보 통합 플랫폼**으로, 관광청, 기차, 고속버스, 카카오 등 다양한 외부 API를 통합하여 사용자에게 풍부한 여행 정보를 제공합니다.

### 🌟 핵심 특징
- **다중 HTTP 클라이언트**: BaseClient 인터페이스로 RestClient + WebClient 둘다 사용ㄱ ㅏ능
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
│  ├── WebClient (구현후 비교후 기각)                               │
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

### 1. 🎭 다중 HTTP 클라이언트 아키텍처

#### **BaseApiClient 인터페이스**
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

#### **RestClient 구현체 (사용중)**
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

### 2. 🎯 전문화된 API 클라이언트

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

#### **API 로깅 DB에 동기화**
```java
@Slf4j
@Component
public class ApiLogUtil {

    private final JdbcTemplate jdbcTemplate;
    private final ExecutorService executorService;
    
    public ApiLogUtil(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.executorService = Executors.newSingleThreadExecutor();
    }
    
    /**
     * API 로그를 비동기적으로 저장
     * 메인 로직의 성능에 영향을 주지 않도록 별도 스레드에서 실행
     */
    public void saveApiLog(ApiLogDto apiLogDto) {
        executorService.submit(() -> {
            try {
                String sql = "INSERT INTO API_LOG (LOG_ID, API_NAME, REQUEST_URL, REQUEST_PARAMS, " +
                        "RESPONSE_CODE, RESPONSE_MESSAGE, RESPONSE_DATA, ITEM_COUNT, IS_SUCCESS, " +
                        "EXECUTION_TIME, REQUEST_TIME, RESPONSE_TIME, ERROR_MESSAGE) " +
                        "VALUES (API_LOG_SEQ.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                
                jdbcTemplate.update(sql,
                        apiLogDto.getApiName(),
                        apiLogDto.getRequestUrl(),
                        apiLogDto.getRequestParams(),
                        apiLogDto.getResponseCode(),
                        apiLogDto.getResponseMessage(),
                        apiLogDto.getResponseData(),
                        apiLogDto.getItemCount(),
                        apiLogDto.getIsSuccess() ? 1 : 0,
                        apiLogDto.getExecutionTime(),
                        apiLogDto.getRequestTime(),
                        apiLogDto.getResponseTime(),
                        apiLogDto.getErrorMessage());
                
                log.debug("API 로그가 저장되었습니다: {}", apiLogDto.getApiName());
            } catch (Exception e) {
                log.error("API 로그 저장 중 오류 발생: {}", e.getMessage(), e);
            }
        });
    }
}
```

### 4. 🚀 성능 비교 시스템

#### **성능 테스트 컨트롤러**
```java
@Slf4j
@RestController
@RequestMapping("/api/test")
public class ApiPerformanceTestController {

    @Autowired
    @Qualifier("restClientApiClient")
    private RestClientApiClient restClientApiClient;

    @Autowired
    @Qualifier("webClientApiClient")
    private WebClientBaseApiClient webClientApiClient;

    /**
     * RestClient와 WebClient 성능 비교 테스트
     */
    @GetMapping("/performance")
    public Map<String, Object> comparePerformance(@RequestParam(defaultValue = "5") int testCount) {
        Map<String, Object> result = new HashMap<>();
        
        log.info("=== API 성능 비교 테스트 시작 (테스트 횟수: {}) ===", testCount);
        
        String testPath = "/areaCode2";
        
        // RestClient 테스트
        Map<String, Object> restClientResults = testRestClient(testPath, testCount);
        
        // WebClient 테스트
        Map<String, Object> webClientResults = testWebClient(testPath, testCount);
        
        // 결과 비교
        result.put("testCount", testCount);
        result.put("restClient", restClientResults);
        result.put("webClient", webClientResults);
        result.put("comparison", compareResults(restClientResults, webClientResults));
        
        log.info("=== API 성능 비교 테스트 완료 ===");
        return result;
    }

    /**
     * RestClient 성능 테스트
     */
    private Map<String, Object> testRestClient(String path, int testCount) {
        Map<String, Object> results = new HashMap<>();
        long totalTime = 0;
        long minTime = Long.MAX_VALUE;
        long maxTime = 0;
        int successCount = 0;
        int failCount = 0;
        
        for (int i = 0; i < testCount; i++) {
            LocalDateTime startTime = LocalDateTime.now();
            
            try {
                URI uri = restClientApiClient.makeTourUri(path);
                List<AreaCodeApiDto> data = restClientApiClient.callApi(uri, this::createAreaCodeDto);
                
                long executionTime = ChronoUnit.MILLIS.between(startTime, LocalDateTime.now());
                totalTime += executionTime;
                minTime = Math.min(minTime, executionTime);
                maxTime = Math.max(maxTime, executionTime);
                successCount++;
                
                log.info("RestClient 테스트 {}: {}ms, 데이터 {}개", i + 1, executionTime, data.size());
                
            } catch (Exception e) {
                failCount++;
                log.error("RestClient 테스트 {} 실패: {}", i + 1, e.getMessage());
            }
        }
        
        results.put("totalTime", totalTime);
        results.put("averageTime", successCount > 0 ? totalTime / successCount : 0);
        results.put("minTime", minTime == Long.MAX_VALUE ? 0 : minTime);
        results.put("maxTime", maxTime);
        results.put("successCount", successCount);
        results.put("failCount", failCount);
        
        return results;
    }
}
```

---

## 🔧 설정 관리

### 📋 API 설정 (ApiConfig)
```java
@ConfigurationProperties(prefix = "api")
@Getter
@Setter
public class ApiConfig {
    private String serviceKey;
    private TourApi tour;
    private TrainApi train;
    private ExpressBusApi expressBus;
    private String kakaoBaseUrl;

    @Getter
    @Setter
    public static class TourApi {
        private String baseUrl;
    }

    @Getter
    @Setter
    public static class TrainApi {
        private String baseUrl;
    }

    @Getter
    @Setter
    public static class ExpressBusApi {
        private String baseUrl;
    }
}
```

### ⚙️ HTTP 클라이언트 설정

#### **RestClient 설정**
```java
@Configuration
public class RestClientConfig {
    
    @Bean
    public RestClient restClient() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(30000); // 30초
        requestFactory.setReadTimeout(60000);    // 60초
        
        return RestClient.builder()
                .requestFactory(requestFactory)
                .defaultHeader("User-Agent", "Dugaza-API-Client")
                .build();
    }
}
```

#### **WebClient 설정**
```java
@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient() {
        HttpClient httpClient = HttpClient
                .create().responseTimeout(Duration.ofSeconds(60))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 30000);

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(32 * 1024 * 1024))
                .build();
    }
}
```

---

## 📊 API 목록

### 🏛️ 관광청 API
| API | 설명 | 엔드포인트 | 
|-----|------|------------|
| 지역 코드 | 전국 지역 코드 조회 | `/areaCode2` |
| 카테고리 코드 | 카테고리 코드 조회 | `/categoryCode2` | 
| 관광지 검색 | 지역별 관광지 검색 | `/searchCategory1` |
| 관광지 상세 | 관광지 상세 정보 | `/detailCommon1` | 
| 관광지 소개 | 관광지 소개 정보 | `/detailIntro2` |
| 숙박 검색 | 지역별 숙박 검색 | `/searchStay2` | 
| 숙박 상세 | 숙박 상세 정보 | `/detailCommon1` |
| 숙박 소개 | 숙박 소개 정보 | `/detailIntro2` | 
| 음식점 검색 | 지역별 음식점 검색 | `/searchRestaurant1` | 
| 음식점 상세 | 음식점 상세 정보 | `/detailCommon1` | 
| 음식점 소개 | 음식점 소개 정보 | `/detailIntro2` |
| 이벤트 검색 | 지역별 이벤트 검색 | `/searchFestival2` | 
| 이벤트 상세 | 이벤트 상세 정보 | `/detailCommon1` | 
| 이벤트 소개 | 이벤트 소개 정보 | `/detailIntro2` |
| 여행코스 소개 | 여행코스 소개 정보 | `/detailIntro2` | 
| 레포츠 소개 | 레포츠 소개 정보 | `/detailIntro2` | 
| 문화시설 소개 | 문화시설 소개 정보 | `/detailIntro2` |
| 쇼핑 소개 | 쇼핑 소개 정보 | `/detailIntro2` | 

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

---

## 🚀 사용 방법

### 1. **기본 API 호출**
```java
@RestController
@RequestMapping("/api/tour")
@RequiredArgsConstructor
public class TourController {

    private final TourApiClient tourApiClient;

    @GetMapping("/areas")
    public ResponseEntity<List<AreaCodeApiDto>> getAreas() {
        List<AreaCodeApiDto> areas = tourApiClient.getAreaCodeData();
        return ResponseEntity.ok(areas);
    }

    @GetMapping("/attractions")
    public ResponseEntity<List<TouristAttractionApiDto>> getAttractions(
            @RequestParam String areaCode,
            @RequestParam String contentTypeId) {
        List<TouristAttractionApiDto> attractions = 
                tourApiClient.getTouristAttractionData(areaCode, contentTypeId);
        return ResponseEntity.ok(attractions);
    }
}
```

## 📈 성능 및 모니터링

### 🔍 로깅 시스템
```java
// API 호출 로깅
log.debug("RestClient API 호출 시작: {}", uri);
log.debug("RestClient 응답 수신: {} 개의 항목", result.size());

// 성능 측정
log.info("[RestClientAPI] getAreaCodeData - 실행 시간: 245ms");
log.info("[WebClientAPI] getAreaCodeData - 실행 시간: 312ms");
```

---


## 🎉 결론

DUGAZA의 API 시스템은 **다양한 외부 API를 하나의 인터페이스로 통합**하여 개발 효율성과 시스템 안정성을 모두 확보했습니다:

### ✅ **성과**
- **통합 인터페이스**: 모든 외부 API를 일관된 방식으로 호출
- **성능 최적화**: RestClient/WebClient 성능 비교 및 최적화
- **실시간 모니터링**: AOP 기반 실행 시간 측정 및 로깅
- **확장성**: 새로운 API 쉽게 추가 가능
- **안정성**: 비동기 로깅, 예외 처리, 타임아웃 설정


이 시스템을 통해 개발자는 **간편한 API 호출**을, 운영자는 **실시간 모니터링**을, 사용자는 **빠른 응답**을 경험할 수 있습니다.

---