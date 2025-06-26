package kr.spring.api.service.impl;

import kr.spring.api.client.TrainApiClient;
import kr.spring.api.dto.TrainCityApiDto;
import kr.spring.api.dto.TrainKindApiDto;
import kr.spring.api.dto.TrainRouteApiDto;
import kr.spring.api.dto.TrainStationApiDto;
import kr.spring.api.mapper.TrainCityApiMapper;
import kr.spring.api.mapper.TrainKindApiMapper;
import kr.spring.api.mapper.TrainRouteApiMapper;
import kr.spring.api.mapper.TrainStationApiMapper;
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
    private final TrainStationApiMapper trainStationApiMapper;
    private final TrainRouteApiMapper trainRouteApiMapper;

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

    @Override
    public Map<String, Object> syncStationCode() {
        AtomicInteger insertCount = new AtomicInteger(0);
        AtomicInteger failedCount = new AtomicInteger(0);
        AtomicInteger updateCount = new AtomicInteger(0);
        log.info("-----> Station Code Sync Service Start");
        List<TrainCityApiDto> trainCityList = trainCityApiMapper.getCityCode();
        log.info("-----> trainCityList size : {}", trainCityList.size());
        log.info("-----> get Station Code Start");
        for (TrainCityApiDto trainCityApiDto : trainCityList) {
            log.info("----------> get Station Code Start cityCode : {}", trainCityApiDto.getCityCode());
            List<TrainStationApiDto> trainStationList = trainApiClient.getTrainStationData(trainCityApiDto.getCityCode());
            log.info("----------> mapping start {} , station size : {}", trainCityApiDto.getCityCode(), trainStationList.size());
            for(TrainStationApiDto trainStationApiDto : trainStationList) {
                if(trainStationApiMapper.insert(trainStationApiDto) != 1) {
                    log.warn("-----------> Train Station Code exist nodeId : {}", trainStationApiDto.getNodeId());
                    if(trainStationApiMapper.update(trainStationApiDto) != 1) {
                        log.error("-----------> station code mapping failed nodeId : {}, nodeName : {}", trainStationApiDto.getNodeId(), trainStationApiDto.getNodeName());
                        failedCount.incrementAndGet();
                        continue;
                    }
                    updateCount.incrementAndGet();
                    continue;
                }
                insertCount.incrementAndGet();
            }
            log.info("----------> mapping end cityCode : {}", trainCityApiDto.getCityCode());
        }
        return Map.of("insert", insertCount.get(),
                "update", updateCount.get(),
                "failed", failedCount.get(),
                "total", insertCount.get() + updateCount.get() + failedCount.get());
    }

    @Override
    public Map<String, Object> syncTrainRouteData() {
        AtomicInteger insertCount = new AtomicInteger(0);
        AtomicInteger failedCount = new AtomicInteger(0);
        AtomicInteger updateCount = new AtomicInteger(0);
        log.info("-----> Station Code Sync Service Start");
        List<TrainStationApiDto> trainCityList = trainStationApiMapper.getAllStation();
        for(int i = 0 ; i < trainCityList.size() ; i++) {
            for(int j = trainCityList.size()-1 ; j >= 0 ; j--) {
                TrainStationApiDto startStation = trainCityList.get(i);
                TrainStationApiDto destStation = trainCityList.get(j);
                List<TrainRouteApiDto> trainRouteData = trainApiClient.getTrainRouteData(startStation.getNodeId(), destStation.getNodeId());
                for(TrainRouteApiDto trainRouteApiDto : trainRouteData) {
                    log.info("----------> mapping start startStation : {}, destStation : {}", startStation.getNodeName(), destStation.getNodeName());
                    if(trainRouteApiMapper.insert(trainRouteApiDto) != 1){
                        log.warn("-----------> Train Route Code exist startStation : {}, destStation : {}", startStation.getNodeName(), destStation.getNodeName());
                        if(trainRouteApiMapper.update(trainRouteApiDto) != 1) {
                            log.error("-----------> train route code mapping failed startStation : {}, destStation : {}", startStation.getNodeName(), destStation.getNodeName());
                            failedCount.incrementAndGet();
                            continue;
                        }
                        updateCount.incrementAndGet();
                        continue;
                    }
                    insertCount.incrementAndGet();
                }
            }
        }
        int total = insertCount.get() + updateCount.get() + failedCount.get();

        log.info("-----> Station Code Sync Service End total : {}, insert : {}, update : {}, failed : {}", total, insertCount.get(), updateCount.get(), failedCount.get());
        return Map.of("insert", insertCount.get(),
                "update", updateCount.get(),
                "failed", failedCount.get(),
                "total", total);
    }
}