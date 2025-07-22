package kr.spring.api.service.impl;

import kr.spring.aop.LogExecutionTime;
import kr.spring.api.client.TourApiClient;
import kr.spring.api.dto.TourApiDto;
import kr.spring.api.mapper.TourApiMapper;
import kr.spring.api.service.CommonDataSyncSupportService;
import kr.spring.api.service.TourSyncService;
import kr.spring.common.ContentTypeid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
@RequiredArgsConstructor
public class TourSyncServiceImpl implements TourSyncService {

    private final TourApiClient tourApiClient;
    private final TourApiMapper tourApiMapper;
    private final CommonDataSyncSupportService common;

    @Override
    @Transactional
    @LogExecutionTime(category = "TourSync")
    public Map<String, Object> getAllTourData() {
        Map<String, Object> result = new HashMap<>();
        result.put("insertCount", 0);
        result.put("updateCount", 0);
        result.put("failedCount", 0);

        for(ContentTypeid contentTypeId : ContentTypeid.values()) {
            Long code = (long) contentTypeId.getCode();
            // 각 콘텐츠 타입별로 바로 매핑 처리
            List<TourApiDto> tourList = tourApiClient.getTouristData(code);
            Map<String, Object> process = common.processDataListToDB(tourApiMapper, tourList);
            result.compute("insertCount", (k, v) -> (int)v + (int) process.getOrDefault("insertCount", 0));
            result.compute("updateCount", (k, v) -> (int)v + (int) process.getOrDefault("updateCount", 0));
            result.compute("failedCount", (k, v) -> (int)v + (int) process.getOrDefault("failedCount", 0));
        }
        return result;
    }


    @Override
    @LogExecutionTime(category = "TourSync")
    public Map<String,Object> getTouristData(ContentTypeid contentTypeId){
        int code = contentTypeId.getCode();
        List<TourApiDto> tourList = tourApiClient.getTouristData((long) code);
        return common.processDataListToDB(tourApiMapper,tourList);
    }

    @Override
    @LogExecutionTime(category = "TourSync")
    public Map<String, Object> updateTourData() {
        List<TourApiDto> tourList = tourApiClient.updateTouristData();
        return common.processDataListToDB(tourApiMapper,tourList);
    }
}
