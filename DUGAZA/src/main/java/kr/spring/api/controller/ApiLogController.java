package kr.spring.api.controller;

import com.project.dugaza.api.dto.ApiLogDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/logs")
@RequiredArgsConstructor
public class ApiLogController {

    private final JdbcTemplate jdbcTemplate;
    
    @GetMapping
    public List<ApiLogDto> getApiLogs(
            @RequestParam(required = false) String apiName,
            @RequestParam(required = false) Boolean isSuccess,
            @RequestParam(defaultValue = "20") int limit) {
        
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM API_LOG WHERE 1=1 ");
        
        if (apiName != null && !apiName.isEmpty()) {
            sql.append("AND API_NAME = '").append(apiName).append("' ");
        }
        
        if (isSuccess != null) {
            sql.append("AND IS_SUCCESS = ").append(isSuccess ? "1" : "0").append(" ");
        }
        
        sql.append("ORDER BY REQUEST_TIME DESC ");
        sql.append("FETCH FIRST ").append(limit).append(" ROWS ONLY");
        
        try {
            return jdbcTemplate.query(sql.toString(), new ApiLogRowMapper());
        } catch (Exception e) {
            log.error("API 로그 조회 중 오류 발생: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    private static class ApiLogRowMapper implements RowMapper<ApiLogDto> {
        @Override
        public ApiLogDto mapRow(ResultSet rs, int rowNum) throws SQLException {
            return ApiLogDto.builder()
                    .logId(rs.getLong("LOG_ID"))
                    .apiName(rs.getString("API_NAME"))
                    .requestUrl(rs.getString("REQUEST_URL"))
                    .requestParams(rs.getString("REQUEST_PARAMS"))
                    .responseCode(rs.getString("RESPONSE_CODE"))
                    .responseMessage(rs.getString("RESPONSE_MESSAGE"))
                    .responseData(rs.getString("RESPONSE_DATA"))
                    .itemCount(rs.getInt("ITEM_COUNT"))
                    .isSuccess(rs.getInt("IS_SUCCESS") == 1)
                    .executionTime(rs.getLong("EXECUTION_TIME"))
                    .requestTime(rs.getObject("REQUEST_TIME", LocalDateTime.class))
                    .responseTime(rs.getObject("RESPONSE_TIME", LocalDateTime.class))
                    .errorMessage(rs.getString("ERROR_MESSAGE"))
                    .build();
        }
    }
} 