package kr.spring.api.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.spring.api.config.ApiConfig;
import kr.spring.api.dto.ApiLogDto;
import kr.spring.api.dto.HouseApiDto;
import kr.spring.api.util.ApiLogUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;

@Slf4j
@Component
@RequiredArgsConstructor
public class BaseApiClient{

    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final WebClient.Builder builder;
    private final ApiConfig apiConfig;


    @Autowired(required = false)
    private ApiLogUtil apiLogUtil;


    public URI makeUri(String baseUrl, String path, String... params) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl + path)
                .queryParam("serviceKey", apiConfig.getServiceKey())
                .queryParam("MobileOS", "ETC")
                .queryParam("MobileApp", "Dugaza")
                .queryParam("numOfRows", 100)
                .queryParam("_type", "json");

        for (int i = 0; i < params.length; i += 2) {
            if (i + 1 < params.length) {
                builder.queryParam(params[i], params[i + 1]);
            }
        }

        return builder.build(true).toUri();
    }

    public <T> List<T> callApi(URI uri, BiFunction<JsonNode, String, T> dtoCreator) {
        // API 로그 생성
        LocalDateTime requestTime = LocalDateTime.now();
        ApiLogDto apiLog = null;

        if (apiLogUtil != null) {
            String apiName = extractApiName(uri.getPath());
            String requestParams = maskServiceKey(uri.getQuery());
            apiLog = apiLogUtil.createApiLog(apiName, uri.toString(), requestParams);
        }

        try {
            log.info("=====================> API Call Start - URI: {}", uri);

            // API 호출 시도
            String response = webClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            long executionTime = ChronoUnit.MILLIS.between(requestTime, LocalDateTime.now());

            // 응답 유효성 검사
            boolean isValid = validateResponse(response);

            if (!isValid) {
                // 로그 저장 - 실패
                if (apiLogUtil != null && apiLog != null) {
                    apiLogUtil.updateFailLog(apiLog, "ERROR", "Invalid response", executionTime);
                }
                return new ArrayList<>();
            }

            // 응답 파싱
            List<T> result = parseApiResponse(response, dtoCreator);

            log.info("=====================> API Call Done - Total : {}", result.size());

            // 로그 저장 - 성공
            if (apiLogUtil != null && apiLog != null) {
                String responsePreview = response != null ?
                        (response.length() > 1000 ? response.substring(0, 1000) + "..." : response) : "null";
                apiLogUtil.updateSuccessLog(apiLog, responsePreview, result.size(), executionTime);
            }

            return result;
        } catch (Exception e) {
            log.error("API 호출 중 오류 발생: {}", e.getMessage(), e);

            // 로그 저장 - 예외
            if (apiLogUtil != null && apiLog != null) {
                long executionTime = ChronoUnit.MILLIS.between(requestTime, LocalDateTime.now());
                apiLogUtil.updateFailLog(apiLog, "EXCEPTION", e.getMessage(), executionTime);
            }

            return new ArrayList<>();
        }
    }

    public <T> List<T> callApiManyTimes(URI uri, BiFunction<JsonNode, String, T> dtoCreator) {
        // 기본 지역변수 초기화
        AtomicInteger totalCount = new AtomicInteger(0); // 총 수
        AtomicInteger successCount = new AtomicInteger(0); // 성공한 수
        AtomicInteger failedCount = new AtomicInteger(0); // 실패한 수
        List<T> allResults = new ArrayList<>();
        int pageNo = 1;
        int totalPages = 1;

        log.info("====================> Call Api with total count Start");

        List<T> pageResults = callApiWithTotalCount(uri, dtoCreator, totalCount);
        allResults.addAll(pageResults);
        totalPages = calculateTotalPages(totalCount.get());

        log.info("====================> first page call end - totalPages : {}, totalCount : {}", totalPages, totalCount.get());

        if(totalPages == 0){
            log.info("====================> totalPages is zero");
            return allResults;
        }
        
        for(pageNo = 2; pageNo <= totalPages; pageNo++){
            log.debug("====================> pageNo : {}", pageNo);
            
            // API 호출 사이에 대기시간 추가 (100ms)
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                log.warn("대기 중 인터럽트 발생", e);
                Thread.currentThread().interrupt();
            }
            
            URI pageUri = UriComponentsBuilder.fromUri(uri)
                    .queryParam("pageNo", pageNo)
                    .build(true)
                    .toUri();
                    
            pageResults = callApi(pageUri, dtoCreator);
            allResults.addAll(pageResults);

            log.debug("====================> pageNo : {} succeed, pageResults : {}", pageNo, pageResults.size());
        }
        
        log.info("====================> Call Api with total count End - 총 결과 수: {}", allResults.size());
        return allResults;
    }

    private int calculateTotalPages(int totalCount) {
        int totalPages = totalCount / 100;
        if (totalCount % 100 > 0) {
            totalPages++;
        }
        return totalPages;
    }

    /**
     * 전체 항목 수를 파악하기 위한 API 호출 메서드
     */
    private <T> List<T> callApiWithTotalCount(URI uri, BiFunction<JsonNode, String, T> dtoCreator, AtomicInteger totalCount) {
        try {
            String response = webClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            if (!validateResponse(response)) {
                return new ArrayList<>();
            }

            // 전체 항목 수 추출
            JsonNode root = objectMapper.readTree(response);
            JsonNode bodyNode = root.path("response").path("body");
            int total = bodyNode.path("totalCount").asInt(0);
            totalCount.set(total);

            // 응답 파싱
            return parseApiResponse(response, dtoCreator);
        } catch (Exception e) {
            log.error("API 호출 중 오류 발생: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    /**
     * 관광 API URI 생성
     */
    public URI makeTourUri(String path, String... params) {
        return makeUri(apiConfig.getTour().getBaseUrl(), path, params);
    }

    /**
     * 기차 API URI 생성
     */
    public URI makeTrainUri(String path, String... params) {
        return makeUri(apiConfig.getTrain().getBaseUrl(), path, params);
    }



    // 유틸리티 메서드들
    private boolean validateResponse(String response) {
        // 응답 유효성 검사 로직
        if (response == null) {
            log.error("API 응답이 null입니다");
            return false;
        }

        if (response.trim().startsWith("<")) {
            log.error("API가 XML 형식으로 응답했습니다. 오류 메시지일 가능성이 높습니다.");
            log.error("응답 내용: {}", response);
            return false;
        }

        return true;
    }

    private <T> List<T> parseApiResponse(String response, BiFunction<JsonNode, String, T> dtoCreator) {
        try {
            JsonNode root = objectMapper.readTree(response);

            if (root.has("response")) {
                JsonNode items = root.path("response").path("body").path("items").path("item");
                List<T> contents = new ArrayList<>();

                if (items.isArray()) {
                    for (JsonNode item : items) {
                        try {
                            T dto = dtoCreator.apply(item, "array");
                            if (dto != null) {
                                contents.add(dto);
                            }
                        } catch (Exception e) {
                            log.error("DTO 생성 중 오류: {}", e.getMessage(), e);
                        }
                    }
                } else if (!items.isMissingNode() && !items.isNull()) {
                    try {
                        T dto = dtoCreator.apply(items, "single");
                        if (dto != null) {
                            contents.add(dto);
                        }
                    } catch (Exception e) {
                        log.error("DTO 생성 중 오류: {}", e.getMessage(), e);
                    }
                }

                return contents;
            }

            return new ArrayList<>();
        } catch (Exception e) {
            log.error("JSON 파싱 오류: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    private String extractApiName(String path) {
        if (path == null || path.isEmpty()) {
            return "unknown";
        }
        String[] parts = path.split("/");
        return parts.length > 0 ? parts[parts.length - 1] : path;
    }

    private String maskServiceKey(String query) {
        if (query == null || query.isEmpty()) {
            return "";
        }
        return query.replaceAll("serviceKey=[^&]+", "serviceKey=*****");
    }
}
