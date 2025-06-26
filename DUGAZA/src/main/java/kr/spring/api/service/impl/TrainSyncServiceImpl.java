package kr.spring.api.service.impl;

import kr.spring.api.client.TrainApiClient;
import kr.spring.api.dto.TrainCityApiDto;
import kr.spring.api.dto.TrainKindApiDto;
import kr.spring.api.mapper.TrainCityApiMapper;
import kr.spring.api.mapper.TrainKindApiMapper;
import kr.spring.api.service.TrainSyncService;
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
public class TrainSyncServiceImpl implements TrainSyncService {

    private final TrainApiClient trainApiClient;
    private final TrainKindApiMapper trainKindApiMapper;
    private final TrainCityApiMapper trainCityApiMapper;

    @Override
    public Map<String, Object> syncTrainKindData() {
        log.info("-----> Train Kind Sync Service Start");
        List<TrainKindApiDto> trainKindList = trainApiClient.getTrainKindData();
        int total = trainKindList.size();
        log.info("----------> Train Kind Insert Or Update Service Start, Train Kind Data Count: {}", total);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failedCount = new AtomicInteger(0);
        AtomicInteger updateCount = new AtomicInteger(0);
        for (TrainKindApiDto trainApiDto : trainKindList) {
            log.info("----------> Train Kind Mapping Start total : {}", total);
            if (trainKindApiMapper.insert(trainApiDto) != 1) {
                log.debug("----------> existing Train Kind try to update vehicle Id : {} ", trainApiDto.getVehicleKindId());
                if (trainKindApiMapper.update(trainApiDto) != 1) {
                    log.warn("---------->failed to mapping Train Kind, vehicle Id : {}", trainApiDto.getVehicleKindId());
                    failedCount.incrementAndGet();
                    continue;
                }
                updateCount.incrementAndGet();
            }
            successCount.incrementAndGet();
        }
        log.debug("-----> total : {} , success : {} , failed : {} , updated : {}", total, successCount.get(), failedCount.get(), updateCount.get());
        log.info("-----> Train Kind Sync Service End ");
        return Map.of("totalCount", total,
                "successCount", successCount.get(),
                "failedCount", failedCount.get(),
                "updateCount", updateCount.get());

    }

    @Override
    public Map<String, Object> syncTrainAreaCode() {
        AtomicInteger insertCount = new AtomicInteger(0);
        AtomicInteger failedCount = new AtomicInteger(0);
        AtomicInteger updateCount = new AtomicInteger(0);
        log.info("-----> Train Area Code Sync Service Start");
        List<TrainCityApiDto> trainCityList = trainApiClient.getTrainAreaData();
        log.info("----------> Train Area Code Insert Or Update Service Start, Train Area Data Count: {}", trainCityList.size());
        for(TrainCityApiDto trainCityApiDto : trainCityList) {
            if(trainCityApiMapper.insert(trainCityApiDto) != 1){
                log.warn("-----------> Train City Code exist, update train city code : {}", trainCityApiDto.getCityCode());
                if(trainCityApiMapper.update(trainCityApiDto) != 1){
                    log.error("-----------> city code mapping failed : {}", trainCityApiDto.getCityCode());
                    failedCount.incrementAndGet();
                    continue;
                }
                updateCount.incrementAndGet();
                continue;
            }
            insertCount.incrementAndGet();
        }
        log.info("-----> Train Area Code Sync Service End");
        return Map.of("insert", insertCount.get(), "update", updateCount.get(), "failed", failedCount.get(), "total", trainCityList.size());
    }
}