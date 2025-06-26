package kr.spring.api.service.impl;

import com.project.dugaza.api.client.HouseApiClient;
import com.project.dugaza.api.dto.HouseApiDto;
import com.project.dugaza.api.mapper.HouseApiMapper;
import com.project.dugaza.api.service.HouseDataSyncService;
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

    @Override
    @Transactional
    public Map<String, Object> syncHouseData() {
        AtomicInteger totalCount = new AtomicInteger(0);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failedCount = new AtomicInteger(0);
        log.info("-----> House Data Sync Service Start");
        try {
            List<HouseApiDto> houseList = houseApiClient.getHouseDataList();
            log.info("----------> House Data Mapping Service start, 데이터 개수: {}", houseList.size());
            for(HouseApiDto houseApiDto : houseList) {
                totalCount.incrementAndGet();
                try {
                    if (houseApiDto.getContentId() == null) {
                        log.warn("----------> House Data contentId is null, skipping record #{}", totalCount.get());
                        failedCount.incrementAndGet();
                        continue;
                    }
                    
                    // 현재 시간 설정
                    if (houseApiDto.getCreatedAt() == null) {
                        houseApiDto.setCreatedAt(LocalDateTime.now());
                    }
                    if (houseApiDto.getUpdatedAt() == null) {
                        houseApiDto.setUpdatedAt(LocalDateTime.now());
                    }
                    
                    try {
                        log.debug("----------> Inserting house data: {}", houseApiDto);
                        houseApiMapper.insert(houseApiDto);
                        successCount.incrementAndGet();
                    } catch (Exception e) {
                        log.error("----------> House Data insert error: {}", e.getMessage());
                        failedCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    failedCount.incrementAndGet();
                    log.error("----------> House Data Mapping Service error, errornum: {}, contentId: {}, error: {}", 
                            failedCount.get(), 
                            houseApiDto != null ? houseApiDto.getContentId() : "null",
                            e.getMessage(), e);
                }
            }
            log.info("----------> House Data Mapping Service end, totalCount: {}, successCount: {}, failedCount: {}",
                    totalCount.get(), successCount.get(), failedCount.get());
        } catch (Exception e) {
            log.error("-----> House Data Sync Service Error: {}", e.getMessage(), e);
        }
        int totalDelete = houseApiMapper.deleteInvalidHouseData();
        log.info("-----> Delete InvalidHouseData: {}", totalDelete);
        log.info("-----> House Data Sync Service End");
        return Map.of("successCount", successCount.get(),
                "failedCount", failedCount.get(),
                "totalCount", totalCount.get());
    }
}
