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
import kr.spring.common.TrainHubStationEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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

        //모든 루트 저장할 리스트
        List<TrainRouteApiDto> resultRoutesList = new ArrayList<>();

        //주요 역끼리의 루트 정보
        for(TrainHubStationEnum depHubStation : TrainHubStationEnum.values()) {
            for(TrainHubStationEnum arrHubStation : TrainHubStationEnum.values()) {
                if(depHubStation == arrHubStation) {
                    continue;
                }
                List<TrainRouteApiDto> getHubRoute = trainApiClient.getTrainRouteData(depHubStation.getNodeId(), arrHubStation.getNodeId());
                resultRoutesList.addAll(getHubRoute);
            }
        }

        log.debug("수신된 resultRoutesList: {}", resultRoutesList);

        //주요역 에서 각 지방이 가지고있는 일반역간의 루트정보
        for(TrainHubStationEnum hubStation : TrainHubStationEnum.values()) {
            List<TrainStationApiDto> inHubStation = trainStationApiMapper.getStationByCityCode(hubStation.getCityCode()); // 허브역별 연결된 역들
            for(TrainStationApiDto inHub : inHubStation) {
                List<TrainRouteApiDto> NormalToHub = trainApiClient.getTrainRouteData(inHub.getNodeId(), hubStation.getNodeId());
                List<TrainRouteApiDto> HubToNormal = trainApiClient.getTrainRouteData(hubStation.getNodeId(), inHub.getNodeId());
                    resultRoutesList.addAll(NormalToHub);
                    resultRoutesList.addAll(HubToNormal);
            }
        }
        log.debug("받은 노선 개수 : {}", resultRoutesList.size());

        for(TrainRouteApiDto routeElement : resultRoutesList) {
            try {
                trainRouteApiMapper.insert(routeElement);
                insertCount.incrementAndGet();
                continue;
            } catch (Exception e) {
                log.error("INSERT 실패 중복된 노선 , 노선 ID : {} , 출발역 : {} , 도착역 : {}, 기차번호 : {}" , routeElement.getTrainRouteId(),routeElement.getDepPlaceName(), routeElement.getArrPlaceName(), routeElement.getTrainNo());
            }
            try{
                trainRouteApiMapper.update(routeElement);
                updateCount.incrementAndGet();
            } catch (Exception e) {
                log.error("UPDATE도 실패  , 노선 ID : {} , 출발역 : {} , 도착역 : {}, 기차번호 : {}" , routeElement.getTrainRouteId(),routeElement.getDepPlaceName(), routeElement.getArrPlaceName(), routeElement.getTrainNo());
                failedCount.incrementAndGet();
            }
        }

        int total = insertCount.get() + updateCount.get() + failedCount.get();
        log.debug("루트 동기화 결과 : {}개", total);
        return Map.of("insert" , insertCount.get(),
                "update" , updateCount.get(),
                "failed" , failedCount.get(),
                "total" , total);
    }
}