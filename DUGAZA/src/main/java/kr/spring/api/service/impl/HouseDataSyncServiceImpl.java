package kr.spring.api.service.impl;

import kr.spring.aop.LogExecutionTime;
import kr.spring.api.client.HouseApiClient;
import kr.spring.api.dto.HouseApiDto;
import kr.spring.api.mapper.HouseApiMapper;
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

    @Override
    @Transactional
    @LogExecutionTime(category = "HouseSync")
    public Map<String, Object> syncHouseData() {
        AtomicInteger totalCount = new AtomicInteger(0);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failedCount = new AtomicInteger(0);
        
        try {
            List<HouseApiDto> houseList = houseApiClient.getHouseDataList();
            
            for(HouseApiDto houseApiDto : houseList) {
                totalCount.incrementAndGet();
                try {
                    if (houseApiDto.getContentId() == null) {
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
                        houseApiMapper.insert(houseApiDto);
                        successCount.incrementAndGet();
                    } catch (Exception e) {
                        failedCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    failedCount.incrementAndGet();
                }
            }
        } catch (Exception e) {
            // AOP에서 예외 처리
        }
        
        int totalDelete = houseApiMapper.deleteInvalidHouseData();
        
        return Map.of("successCount", successCount.get(),
                "failedCount", failedCount.get(),
                "totalCount", totalCount.get(),
                "deletedCount", totalDelete);
    }
}
