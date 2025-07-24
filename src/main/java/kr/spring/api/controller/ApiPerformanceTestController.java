package kr.spring.api.controller;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;

import kr.spring.api.client.base.RestClientApiClient;
import kr.spring.api.client.base.WebClientBaseApiClient;
import kr.spring.api.dto.AreaCodeApiDto;
import lombok.extern.slf4j.Slf4j;

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
        
        // 지역 코드 API로 테스트
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
        
        log.info("RestClient 테스트 시작...");
        
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
        
        log.info("RestClient 테스트 완료: 평균 {}ms, 성공 {}회, 실패 {}회", 
                results.get("averageTime"), successCount, failCount);
        
        return results;
    }

    /**
     * WebClient 성능 테스트
     */
    private Map<String, Object> testWebClient(String path, int testCount) {
        Map<String, Object> results = new HashMap<>();
        long totalTime = 0;
        long minTime = Long.MAX_VALUE;
        long maxTime = 0;
        int successCount = 0;
        int failCount = 0;
        
        log.info("WebClient 테스트 시작...");
        
        for (int i = 0; i < testCount; i++) {
            LocalDateTime startTime = LocalDateTime.now();
            
            try {
                URI uri = webClientApiClient.makeTourUri(path);
                List<AreaCodeApiDto> data = webClientApiClient.callApi(uri, this::createAreaCodeDto);
                
                long executionTime = ChronoUnit.MILLIS.between(startTime, LocalDateTime.now());
                totalTime += executionTime;
                minTime = Math.min(minTime, executionTime);
                maxTime = Math.max(maxTime, executionTime);
                successCount++;
                
                log.info("WebClient 테스트 {}: {}ms, 데이터 {}개", i + 1, executionTime, data.size());
                
            } catch (Exception e) {
                failCount++;
                log.error("WebClient 테스트 {} 실패: {}", i + 1, e.getMessage());
            }
        }
        
        results.put("totalTime", totalTime);
        results.put("averageTime", successCount > 0 ? totalTime / successCount : 0);
        results.put("minTime", minTime == Long.MAX_VALUE ? 0 : minTime);
        results.put("maxTime", maxTime);
        results.put("successCount", successCount);
        results.put("failCount", failCount);
        
        log.info("WebClient 테스트 완료: 평균 {}ms, 성공 {}회, 실패 {}회", 
                results.get("averageTime"), successCount, failCount);
        
        return results;
    }

    /**
     * 결과 비교
     */
    private Map<String, Object> compareResults(Map<String, Object> restClient, Map<String, Object> webClient) {
        Map<String, Object> comparison = new HashMap<>();
        
        long restAvg = (Long) restClient.get("averageTime");
        long webAvg = (Long) webClient.get("averageTime");
        
        if (restAvg > 0 && webAvg > 0) {
            double improvement = ((double) (webAvg - restAvg) / webAvg) * 100;
            comparison.put("faster", restAvg < webAvg ? "RestClient" : "WebClient");
            comparison.put("improvement", String.format("%.2f%%", Math.abs(improvement)));
            comparison.put("timeDifference", Math.abs(restAvg - webAvg));
        } else {
            comparison.put("faster", "N/A");
            comparison.put("improvement", "N/A");
            comparison.put("timeDifference", 0);
        }
        
        return comparison;
    }

    /**
     * AreaCodeApiDto 생성 함수
     */
    private AreaCodeApiDto createAreaCodeDto(JsonNode item, String type) {
        try {
            AreaCodeApiDto dto = new AreaCodeApiDto();
            dto.setAreaCode(Long.parseLong(item.path("code").asText()));
            dto.setAreaName(item.path("name").asText());
            return dto;
        } catch (NumberFormatException e) {
            return null;
        }
    }
} 