package kr.spring.api.service.impl;

import kr.spring.aop.LogExecutionTime;
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
    @LogExecutionTime(category = "TrainKind")
    @Transactional
    public Map<String, Object> syncTrainKindData() {
        List<TrainKindApiDto> trainKindList = trainApiClient.getTrainKindData();
        int total = trainKindList.size();
        
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failedCount = new AtomicInteger(0);
        AtomicInteger updateCount = new AtomicInteger(0);
        
        for (TrainKindApiDto trainApiDto : trainKindList) {
            try {
                // INSERT 시도
                boolean insertSuccess = false;
                try {
                    insertSuccess = trainKindApiMapper.insert(trainApiDto) == 1;
                    if (insertSuccess) {
                        successCount.incrementAndGet();
                        continue;
                    }
                } catch (Exception e) {
                    log.debug("INSERT 중 예외 발생 (이미 존재하는 키일 가능성): {}", e.getMessage());
                }
                
                // INSERT 실패 또는 예외 발생 시 UPDATE 시도
                if (trainKindApiMapper.update(trainApiDto) != 1) {
                    failedCount.incrementAndGet();
                    continue;
                }
                updateCount.incrementAndGet();
            } catch (Exception e) {
                log.error("기차 종류 데이터 처리 중 예외 발생: {}", e.getMessage(), e);
                failedCount.incrementAndGet();
            }
        }
                
        return Map.of("totalCount", total,
                "successCount", successCount.get(),
                "failedCount", failedCount.get(),
                "updateCount", updateCount.get());
    }

    @Override
    @LogExecutionTime(category = "TrainArea")
    @Transactional
    public Map<String, Object> syncTrainAreaCode() {
        AtomicInteger insertCount = new AtomicInteger(0);
        AtomicInteger failedCount = new AtomicInteger(0);
        AtomicInteger updateCount = new AtomicInteger(0);
        
        List<TrainCityApiDto> trainCityList = trainApiClient.getTrainAreaData();
        log.debug("API에서 가져온 도시 데이터 수: {}", trainCityList.size());
        
        for(TrainCityApiDto trainCityApiDto : trainCityList) {
            try {
                log.debug("도시 데이터 처리 중: {}, 코드: {}", trainCityApiDto.getCityName(), trainCityApiDto.getCityCode());
                
                // INSERT 시도
                boolean insertSuccess = false;
                try {
                    insertSuccess = trainCityApiMapper.insert(trainCityApiDto) == 1;
                    if (insertSuccess) {
                        log.debug("INSERT 성공");
                        insertCount.incrementAndGet();
                        continue;
                    }
                } catch (Exception e) {
                    log.debug("INSERT 중 예외 발생 (이미 존재하는 키일 가능성): {}", e.getMessage());
                }
                
                // INSERT 실패 또는 예외 발생 시 UPDATE 시도
                log.debug("INSERT 실패, UPDATE 시도");
                if(trainCityApiMapper.update(trainCityApiDto) != 1){
                    log.error("UPDATE도 실패: {}", trainCityApiDto);
                    failedCount.incrementAndGet();
                    continue;
                }
                log.debug("UPDATE 성공");
                updateCount.incrementAndGet();
                
            } catch (Exception e) {
                log.error("도시 데이터 처리 중 예외 발생: {}", e.getMessage(), e);
                failedCount.incrementAndGet();
            }
        }
        
        Map<String, Object> result = Map.of("insert", insertCount.get(), 
                "update", updateCount.get(), 
                "failed", failedCount.get(), 
                "total", trainCityList.size());
        
        log.debug("도시 데이터 동기화 결과: {}", result);
        return result;
    }

    @Override
    @LogExecutionTime(category = "Station")
    @Transactional
    public Map<String, Object> syncStationCode() {
        AtomicInteger insertCount = new AtomicInteger(0);
        AtomicInteger failedCount = new AtomicInteger(0);
        AtomicInteger updateCount = new AtomicInteger(0);
        
        List<TrainCityApiDto> trainCityList = trainCityApiMapper.getAllCityDto();
        log.debug("도시 데이터 조회 결과: {} 개의 도시", trainCityList.size());
        
        int processedCities = 0;
        for (TrainCityApiDto trainCityApiDto : trainCityList) {
            processedCities++;
            Long cityCode = trainCityApiDto.getCityCode();
            log.debug("도시 처리 중: {}, 코드: {}, 진행: {}/{}", 
                    trainCityApiDto.getCityName(), cityCode, processedCities, trainCityList.size());
            
            List<TrainStationApiDto> trainStationList = trainApiClient.getTrainStationData(cityCode);
            log.debug("역 데이터 조회 결과: {} 개의 역", trainStationList.size());
            
            for(TrainStationApiDto trainStationApiDto : trainStationList) {
                try {
                    log.debug("역 데이터 처리 중: {}, ID: {}, 도시코드: {}", 
                            trainStationApiDto.getNodeName(), trainStationApiDto.getNodeId(), trainStationApiDto.getCityCode());
                    
                    // INSERT 시도
                    boolean insertSuccess = false;
                    try {
                        insertSuccess = trainStationApiMapper.insert(trainStationApiDto) == 1;
                        if (insertSuccess) {
                            log.debug("INSERT 성공");
                            insertCount.incrementAndGet();
                            continue;
                        }
                    } catch (Exception e) {
                        log.debug("INSERT 중 예외 발생 (이미 존재하는 키일 가능성): {}", e.getMessage());
                    }
                    
                    // INSERT 실패 또는 예외 발생 시 UPDATE 시도
                    log.debug("INSERT 실패, UPDATE 시도");
                    if(trainStationApiMapper.update(trainStationApiDto) != 1) {
                        log.error("UPDATE도 실패: {}", trainStationApiDto);
                        failedCount.incrementAndGet();
                        continue;
                    }
                    log.debug("UPDATE 성공");
                    updateCount.incrementAndGet();
                    
                } catch (Exception e) {
                    log.error("역 데이터 처리 중 예외 발생: {}", e.getMessage(), e);
                    failedCount.incrementAndGet();
                }
            }
        }
        
        int total = insertCount.get() + updateCount.get() + failedCount.get();
        
        Map<String, Object> result = Map.of("insert", insertCount.get(),
                "update", updateCount.get(),
                "failed", failedCount.get(),
                "total", total);
        
        log.debug("역 데이터 동기화 결과: {}", result);
        return result;
    }

    @Override
    @LogExecutionTime(category = "TrainRoute")
    @Transactional
    public Map<String, Object> syncTrainRouteData() {
        AtomicInteger insertCount = new AtomicInteger(0);
        AtomicInteger failedCount = new AtomicInteger(0);
        AtomicInteger updateCount = new AtomicInteger(0);
        
        List<TrainStationApiDto> trainStationList = trainStationApiMapper.getAllStation();
        
        int totalPairs = 0;
        int processedPairs = 0;
        
        // 전체 경로 쌍 개수 계산
        for(int i = 0; i < trainStationList.size(); i++) {
            for(int j = trainStationList.size()-1; j >= 0; j--) {
                if (i != j) totalPairs++;
            }
        }
        
        for(int i = 0; i < trainStationList.size(); i++) {
            for(int j = trainStationList.size()-1; j >= 0; j--) {
                if (i == j) continue; // 같은 역은 건너뛰기
                
                processedPairs++;
                TrainStationApiDto startStation = trainStationList.get(i);
                TrainStationApiDto destStation = trainStationList.get(j);
                
                try {
                    List<TrainRouteApiDto> trainRouteData = trainApiClient.getTrainRouteData(
                            startStation.getNodeId(), destStation.getNodeId());
                    
                    if (trainRouteData.isEmpty()) {
                        continue;
                    }
                    
                    for(TrainRouteApiDto trainRouteApiDto : trainRouteData) {
                        try {
                            // INSERT 시도
                            boolean insertSuccess = false;
                            try {
                                insertSuccess = trainRouteApiMapper.insert(trainRouteApiDto) == 1;
                                if (insertSuccess) {
                                    insertCount.incrementAndGet();
                                    continue;
                                }
                            } catch (Exception e) {
                                log.debug("INSERT 중 예외 발생 (이미 존재하는 키일 가능성): {}", e.getMessage());
                            }
                            
                            // INSERT 실패 또는 예외 발생 시 UPDATE 시도
                            if(trainRouteApiMapper.update(trainRouteApiDto) != 1) {
                                failedCount.incrementAndGet();
                                continue;
                            }
                            updateCount.incrementAndGet();
                        } catch (Exception e) {
                            log.error("노선 데이터 처리 중 예외 발생: {}", e.getMessage(), e);
                            failedCount.incrementAndGet();
                        }
                    }
                } catch (Exception e) {
                    log.error("노선 API 호출 중 예외 발생: {}", e.getMessage(), e);
                    failedCount.incrementAndGet();
                }
            }
        }
        
        int total = insertCount.get() + updateCount.get() + failedCount.get();
                
        return Map.of("insert", insertCount.get(),
                "update", updateCount.get(),
                "failed", failedCount.get(),
                "total", total);
    }
}