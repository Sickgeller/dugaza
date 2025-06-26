package kr.spring.api.service.impl;

import kr.spring.aop.LogExecutionTime;
import kr.spring.api.client.EventApiClient;
import kr.spring.api.dto.EventContentApiDto;
import kr.spring.api.mapper.EventContentMapper;
import kr.spring.api.service.EventDataSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventDataSyncServiceImpl implements EventDataSyncService {

    private final EventApiClient eventApiClient;
    private final EventContentMapper eventContentMapper;

    @Override
    @Transactional
    @LogExecutionTime(category = "EventSync")
    public Map<String, Object> syncEventData(Long startYear) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 테이블이 없는 경우 생성
            eventContentMapper.createEventContentTableIfNotExists();
            
            // API에서 이벤트 데이터 가져오기
            List<EventContentApiDto> eventContentList = eventApiClient.getEventContent(startYear);
            
            if (eventContentList.isEmpty()) {
                result.put("success", false);
                result.put("message", "API에서 이벤트 데이터를 가져오지 못했습니다.");
                return result;
            }
            
            // 이벤트 데이터 저장
            int successCount = 0;
            int failCount = 0;
            
            for (EventContentApiDto eventContent : eventContentList) {
                try {
                    int affected = eventContentMapper.insertOrUpdateEventContent(eventContent);
                    if (affected > 0) {
                        successCount++;
                    } else {
                        failCount++;
                    }
                } catch (Exception e) {
                    failCount++;
                }
            }
            
            // 결과 반환
            int totalCount = eventContentMapper.countEventContent();
            
            result.put("success", true);
            result.put("message", "이벤트 데이터 동기화가 완료되었습니다.");
            result.put("totalApiData", eventContentList.size());
            result.put("successCount", successCount);
            result.put("failCount", failCount);
            result.put("totalDbCount", totalCount);
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "이벤트 데이터 동기화 중 오류가 발생했습니다: " + e.getMessage());
        }
        
        return result;
    }
}


