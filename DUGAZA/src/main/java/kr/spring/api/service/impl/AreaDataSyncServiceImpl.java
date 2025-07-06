package kr.spring.api.service.impl;

import kr.spring.aop.LogExecutionTime;
import kr.spring.api.client.AreaCodeApiClient;
import kr.spring.api.dto.AreaCodeApiDto;
import kr.spring.api.dto.SigunguCodeApiDto;
import kr.spring.api.mapper.AreaCodeMapper;
import kr.spring.api.mapper.SigunguApiMapper;
import kr.spring.api.service.AreaDataSyncService;
import kr.spring.api.service.CommonDataSyncSupportService;
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
public class AreaDataSyncServiceImpl implements AreaDataSyncService {

    private final AreaCodeApiClient areaCodeApiClient;
    private final AreaCodeMapper areaCodeMapper;
    private final SigunguApiMapper sigunguApiMapper;
    private final CommonDataSyncSupportService common;

    @Transactional
    @Override
    @LogExecutionTime(category = "AreaSync")
    public Map<String, Object> syncAreaCodes() {
            List<AreaCodeApiDto> dtoList = areaCodeApiClient.getAreaCode();
            return common.processDataListToDB(areaCodeMapper, dtoList);
        }

    // 추상화 힘듬 직접구현
    @Transactional
    @Override
    @LogExecutionTime(category = "AreaSync")
    public Map<String, Object> syncSigunguCodes() {
        List<AreaCodeApiDto> areaCodeList = areaCodeApiClient.getAreaCode();

        AtomicInteger insertCount = new AtomicInteger(0);
        AtomicInteger updateCount = new AtomicInteger(0);
        AtomicInteger failedCount = new AtomicInteger(0);

        // 지역코드별 시군구 조회
        for (AreaCodeApiDto element : areaCodeList) {
            Long areaCode = element.getAreaCode();
            List<SigunguCodeApiDto> sigunguCodeList = areaCodeApiClient.getSigunguCode(areaCode);

            Map<String, Object> process = common.processDataListToDB(sigunguApiMapper, sigunguCodeList);

            insertCount.addAndGet((int) process.getOrDefault("insertCount", 0));
            updateCount.addAndGet((int) process.getOrDefault("updateCount", 0));
            failedCount.addAndGet((int) process.getOrDefault("failedCount", 0));
        }

        int totalCount = insertCount.get() + updateCount.get() + failedCount.get();

        return Map.of(
                "insertCount", insertCount.get(),
                "updateCount", updateCount.get(),
                "failedCount", failedCount.get(),
                "totalCount", totalCount
        );
    }


}
