package kr.spring.api.service.impl;

import com.project.dugaza.api.client.TourApiClient;
import com.project.dugaza.api.dto.TourApiDto;
import com.project.dugaza.api.mapper.TourApiMapper;
import com.project.dugaza.api.service.TourSyncService;
import com.project.dugaza.common.ContentTypeid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
@RequiredArgsConstructor
public class TourSyncServiceImpl implements TourSyncService {

    private final TourApiClient tourApiClient;
    private final TourApiMapper tourApiMapper;

    @Override
    @Transactional
    public Map<String, Object> getAllTourData() {
        log.info("-----> get Every Tour Data Start");
        AtomicInteger mappingTotalCount = new AtomicInteger(0);
        AtomicInteger mappingSuccessCount = new AtomicInteger(0);
        AtomicInteger mappingFailCount = new AtomicInteger(0);
        
        try {
            for(ContentTypeid contentTypeId : ContentTypeid.values()) {
                int code = contentTypeId.getCode();
                String name = contentTypeId.name();
    
                log.info("----------> get contentTypeId : {} , {}", code, name);
                List<TourApiDto> codeResult = tourApiClient.getTouristData(code);
                log.info("----------> done contentTypeId : {}, totalNum : {}", code , codeResult.size());
                
                // 각 콘텐츠 타입별로 바로 매핑 처리
                processTourData(codeResult, mappingTotalCount, mappingSuccessCount, mappingFailCount);
            }
        } catch (Exception e) {
            log.error("관광 데이터 동기화 중 오류 발생: {}", e.getMessage(), e);
        }
        
        log.info("-----> get Every Tour Data End");
        log.info("-----> Tour Data Sync Summary: total={}, success={}, fail={}", 
                mappingTotalCount.get(), mappingSuccessCount.get(), mappingFailCount.get());
                
        return Map.of(
                "totalMappingCount", mappingTotalCount.get(),
                "successMappingCount", mappingSuccessCount.get(),
                "failMappingCount", mappingFailCount.get());
    }
    
    private void processTourData(List<TourApiDto> tourList, 
                               AtomicInteger totalCount, 
                               AtomicInteger successCount, 
                               AtomicInteger failCount) {
        for(TourApiDto dto : tourList) {
            int current = totalCount.incrementAndGet();
            if (current % 100 == 0) {
                log.info("----------> mapping progress: {}/{}", current, tourList.size());
            }
            
            try {
                tourApiMapper.insert(dto);
                successCount.incrementAndGet();
            } catch (Exception e) {
                failCount.incrementAndGet();
                log.error("----------> mapping failed: contentId={}, error={}", 
                        dto.getContentId(), e.getMessage());
            }
        }
    }

    @Override
    public Map<String,Object> getTouristData(ContentTypeid contentTypeId){
        String name = contentTypeId.name();
        int code = contentTypeId.getCode();
        log.info("-----> Get {} Data Start contentTypeId : {}", name, code);
        List<TourApiDto> tourList = tourApiClient.getTouristData(code);
        log.info("-----> Get {} Data End contentTypeId : {}, count: {}", name, code, tourList.size());
        
        AtomicInteger totalCount = new AtomicInteger(0);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);
        
        processTourData(tourList, totalCount, successCount, failCount);
        
        return Map.of(
                "contentTypeId", code,
                "contentTypeName", name,
                "totalCount", totalCount.get(),
                "successCount", successCount.get(),
                "failCount", failCount.get());
    }
}
