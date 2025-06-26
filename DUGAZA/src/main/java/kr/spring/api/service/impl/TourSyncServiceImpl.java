package kr.spring.api.service.impl;

import kr.spring.aop.LogExecutionTime;
import kr.spring.api.client.TourApiClient;
import kr.spring.api.dto.TourApiDto;
import kr.spring.api.mapper.TourApiMapper;
import kr.spring.api.service.TourSyncService;
import kr.spring.common.ContentTypeid;
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
    @LogExecutionTime(category = "TourSync")
    public Map<String, Object> getAllTourData() {
        AtomicInteger mappingTotalCount = new AtomicInteger(0);
        AtomicInteger mappingSuccessCount = new AtomicInteger(0);
        AtomicInteger mappingFailCount = new AtomicInteger(0);
        
        try {
            for(ContentTypeid contentTypeId : ContentTypeid.values()) {
                int code = contentTypeId.getCode();
                String name = contentTypeId.name();
    
                List<TourApiDto> codeResult = tourApiClient.getTouristData(code);
                
                // 각 콘텐츠 타입별로 바로 매핑 처리
                processTourData(codeResult, mappingTotalCount, mappingSuccessCount, mappingFailCount);
            }
        } catch (Exception e) {
            // AOP에서 예외 처리
        }
                
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
            totalCount.incrementAndGet();
            
            try {
                tourApiMapper.insert(dto);
                successCount.incrementAndGet();
            } catch (Exception e) {
                failCount.incrementAndGet();
            }
        }
    }

    @Override
    @LogExecutionTime(category = "TourSync")
    public Map<String,Object> getTouristData(ContentTypeid contentTypeId){
        String name = contentTypeId.name();
        int code = contentTypeId.getCode();
        List<TourApiDto> tourList = tourApiClient.getTouristData(code);
        
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
