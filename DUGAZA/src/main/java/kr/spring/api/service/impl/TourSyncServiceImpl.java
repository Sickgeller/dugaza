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
        AtomicInteger insertCount = new AtomicInteger(0);
        AtomicInteger updateCount = new AtomicInteger(0);
        AtomicInteger failedCount = new AtomicInteger(0);
        AtomicInteger totalCount = new AtomicInteger(0);


        try {
            for(ContentTypeid contentTypeId : ContentTypeid.values()) {
                int code = contentTypeId.getCode();
                String name = contentTypeId.name();
    
                // 각 콘텐츠 타입별로 바로 매핑 처리
                List<TourApiDto> tourList = tourApiClient.getTouristData(code);
                Map<String, Object> result = processTourData(tourList);

                totalCount.addAndGet(insertCount.addAndGet((int)result.get("insertCount")));
                totalCount.addAndGet(updateCount.addAndGet((int)result.get("updateCount")));
                totalCount.addAndGet(failedCount.addAndGet((int)result.get("failedCount")));

            }
        } catch (Exception e) {
            // AOP에서 예외 처리
        }

        return Map.of("insertCount", insertCount.get(),
                "updateCount" , updateCount.get(),
                "failedCount" , failedCount.get(),
                "totalCount" , totalCount.get());
    }


    @Override
    @LogExecutionTime(category = "TourSync")
    public Map<String,Object> getTouristData(ContentTypeid contentTypeId){
        int code = contentTypeId.getCode();
        List<TourApiDto> tourList = tourApiClient.getTouristData(code);
        processTourData(tourList);
        
        return processTourData(tourList);
    }

    @Override
    @LogExecutionTime(category = "TourSync")
    public Map<String, Object> updateTourData() {
        List<TourApiDto> tourList = tourApiClient.updateTouristData();
        processTourData(tourList);
        return Map.of();
    }


    private Map<String,Object> processTourData(List<TourApiDto> tourList) {

        AtomicInteger insertCount = new AtomicInteger(0);
        AtomicInteger updateCount = new AtomicInteger(0);
        AtomicInteger failedCount = new AtomicInteger(0);

        for(TourApiDto dto : tourList) {

            try {
                tourApiMapper.insert(dto);
                insertCount.incrementAndGet();
                continue;
            } catch (Exception e) {
                log.debug("insert failed", e);
            }

            try{
                tourApiMapper.update(dto);
                updateCount.incrementAndGet();
            } catch (Exception e) {
                log.debug("update failed", e);
                failedCount.incrementAndGet();
            }
        }

        return Map.of("insertCount", insertCount.get(),
                "updateCount" , updateCount.get(),
                "failedCount" , failedCount.get(),
                "totalCount" , insertCount.get() + failedCount.get() + updateCount.get());

    }
}
