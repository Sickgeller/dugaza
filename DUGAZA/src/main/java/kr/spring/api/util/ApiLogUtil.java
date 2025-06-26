package kr.spring.api.util;

import kr.spring.api.dto.ApiLogDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Component
public class ApiLogUtil {

    private final JdbcTemplate jdbcTemplate;
    private final ExecutorService executorService;
    
    @Autowired
    public ApiLogUtil(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.executorService = Executors.newSingleThreadExecutor();
    }
    
    /**
     * API 로그를 비동기적으로 저장합니다.
     * 메인 로직의 성능에 영향을 주지 않도록 별도 스레드에서 실행됩니다.
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
    
    /**
     * API 호출 시작 시 로그 객체를 생성합니다.
     */
    public ApiLogDto createApiLog(String apiName, String url, String params) {
        return ApiLogDto.builder()
                .apiName(apiName)
                .requestUrl(url)
                .requestParams(params)
                .requestTime(LocalDateTime.now())
                .isSuccess(false) // 기본값은 실패로 설정
                .build();
    }
    
    /**
     * API 호출 성공 시 로그 정보를 업데이트합니다.
     */
    public void updateSuccessLog(ApiLogDto apiLogDto, String responseData, int itemCount, long executionTime) {
        apiLogDto.setResponseTime(LocalDateTime.now());
        apiLogDto.setResponseCode("200");
        apiLogDto.setResponseMessage("Success");
        apiLogDto.setResponseData(responseData != null && responseData.length() > 4000 ? 
                responseData.substring(0, 4000) : responseData);
        apiLogDto.setItemCount(itemCount);
        apiLogDto.setExecutionTime(executionTime);
        apiLogDto.setIsSuccess(true);
        
        saveApiLog(apiLogDto);
    }
    
    /**
     * API 호출 실패 시 로그 정보를 업데이트합니다.
     */
    public void updateFailLog(ApiLogDto apiLogDto, String errorCode, String errorMessage, long executionTime) {
        apiLogDto.setResponseTime(LocalDateTime.now());
        apiLogDto.setResponseCode(errorCode);
        apiLogDto.setResponseMessage("Error");
        apiLogDto.setErrorMessage(errorMessage);
        apiLogDto.setExecutionTime(executionTime);
        apiLogDto.setIsSuccess(false);
        
        saveApiLog(apiLogDto);
    }
} 