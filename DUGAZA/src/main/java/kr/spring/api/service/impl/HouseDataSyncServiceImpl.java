package kr.spring.api.service.impl;

import kr.spring.aop.LogExecutionTime;
import kr.spring.api.client.HouseApiClient;
import kr.spring.api.dto.HouseApiDto;
import kr.spring.api.mapper.HouseApiMapper;
import kr.spring.api.service.CommonDataSyncSupportService;
import kr.spring.api.service.HouseDataSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
@RequiredArgsConstructor
public class HouseDataSyncServiceImpl implements HouseDataSyncService {

    private final HouseApiClient houseApiClient;
    private final HouseApiMapper houseApiMapper;
    private final CommonDataSyncSupportService common;

    @Override
    @Transactional
    @LogExecutionTime(category = "HouseSync")
    public Map<String, Object> syncHouseData() {
        List<HouseApiDto> houseList = houseApiClient.getHouseDataList();
        return common.processDataListToDB(houseApiMapper, houseList);
    }
}
