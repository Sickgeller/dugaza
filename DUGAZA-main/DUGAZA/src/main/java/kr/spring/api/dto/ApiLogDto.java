package kr.spring.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiLogDto {
    private Long logId;
    private String apiName;
    private String requestUrl;
    private String requestParams;
    private String responseCode;
    private String responseMessage;
    private String responseData;
    private Integer itemCount;
    private Boolean isSuccess;
    private Long executionTime;
    private LocalDateTime requestTime;
    private LocalDateTime responseTime;
    private String errorMessage;
} 