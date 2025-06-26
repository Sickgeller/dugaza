package kr.spring.api.service.impl;

import com.project.dugaza.api.client.EventApiClient;
import com.project.dugaza.api.dto.EventContentApiDto;
import com.project.dugaza.api.mapper.EventContentMapper;
import com.project.dugaza.api.service.EventDataSyncService;
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
    public Map<String, Object> syncEventCode(Long startYear) {
        log.info("이벤트 데이터 동기화 시작 - 시작 연도: {}", startYear);
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 테이블이 없는 경우 생성
            eventContentMapper.createEventContentTableIfNotExists();
            log.info("이벤트 테이블 확인/생성 완료");
            
            // API에서 이벤트 데이터 가져오기
            List<EventContentApiDto> eventContentList = eventApiClient.getEventContent(startYear);
            log.info("API에서 이벤트 데이터 {} 건 조회 완료", eventContentList.size());
            
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
                        log.warn("이벤트 데이터 저장 실패: {}", eventContent.getContentid());
                    }
                } catch (Exception e) {
                    failCount++;
                    log.error("이벤트 데이터 저장 중 오류 발생: {}", e.getMessage(), e);
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
            
            log.info("이벤트 데이터 동기화 완료 - 성공: {}, 실패: {}, DB 총 개수: {}", 
                    successCount, failCount, totalCount);
            
        } catch (Exception e) {
            log.error("이벤트 데이터 동기화 중 오류 발생: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("message", "이벤트 데이터 동기화 중 오류가 발생했습니다: " + e.getMessage());
        }
        
        return result;
    }
}


