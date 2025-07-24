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
import kr.spring.api.service.CommonDataSyncSupportService;
import kr.spring.api.service.TrainSyncService;
import kr.spring.common.TrainHubStationEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class TrainSyncServiceImpl implements TrainSyncService {

    private final TrainApiClient trainApiClient;
    private final TrainKindApiMapper trainKindApiMapper;
    private final TrainCityApiMapper trainCityApiMapper;
    private final TrainStationApiMapper trainStationApiMapper;
    private final TrainRouteApiMapper trainRouteApiMapper;
    private final CommonDataSyncSupportService common;

    @Override
    @LogExecutionTime(category = "TrainKind")
    @Transactional
    public Map<String, Object> syncTrainKindData() {
        List<TrainKindApiDto> trainKindList = trainApiClient.getTrainKindData();
        return common.processDataListToDB(trainKindApiMapper, trainKindList);
    }

    @Override
    @LogExecutionTime(category = "TrainArea")
    @Transactional
    public Map<String, Object> syncTrainAreaCode() {
        List<TrainCityApiDto> trainCityList = trainApiClient.getTrainAreaData();
        return common.processDataListToDB(trainCityApiMapper, trainCityList);
    }

    @Override
    @LogExecutionTime(category = "Station")
    @Transactional
    public Map<String, Object> syncStationCode() {
        Map<String, Object> result = new HashMap<>();

        List<TrainCityApiDto> trainCityList = trainCityApiMapper.getAllCityDto();
        log.debug("도시 데이터 조회 결과: {} 개의 도시", trainCityList.size());

        for (TrainCityApiDto trainCityApiDto : trainCityList) {
            Long cityCode = trainCityApiDto.getCityCode();
            List<TrainStationApiDto> trainStationList = trainApiClient.getTrainStationData(cityCode);
            Map<String,Object> process = common.processDataListToDB(trainStationApiMapper, trainStationList);
            result.compute("insertCount", (k, v) -> (int)v + (int) process.getOrDefault("insertCount", 0));
            result.compute("updateCount", (k, v) -> (int)v + (int) process.getOrDefault("updateCount", 0));
            result.compute("failedCount", (k, v) -> (int)v + (int) process.getOrDefault("failedCount", 0));
        }
        return result;
    }

    @Override
    @LogExecutionTime(category = "TrainRoute")
    @Transactional
    public Map<String, Object> syncTrainRouteData() {
        //모든 루트 저장할 리스트
        List<TrainRouteApiDto> resultRoutesList = new ArrayList<>();
        //주요 역끼리의 루트 정보
        for (TrainHubStationEnum depHubStation : TrainHubStationEnum.values()) {
            for (TrainHubStationEnum arrHubStation : TrainHubStationEnum.values()) {
                if (depHubStation == arrHubStation) {
                    continue;
                }
                List<TrainRouteApiDto> getHubRoute = trainApiClient.getTrainRouteData(depHubStation.getNodeId(), arrHubStation.getNodeId());
                resultRoutesList.addAll(getHubRoute);
            }
        }
        log.debug("수신된 resultRoutesList: {}", resultRoutesList);

        //주요역 에서 각 지방이 가지고있는 일반역간의 루트정보
        for (TrainHubStationEnum hubStation : TrainHubStationEnum.values()) {
            List<TrainStationApiDto> inHubStation = trainStationApiMapper.getStationByCityCode(hubStation.getCityCode()); // 허브역별 연결된 역들
            for (TrainStationApiDto inHub : inHubStation) {
                List<TrainRouteApiDto> NormalToHub = trainApiClient.getTrainRouteData(inHub.getNodeId(), hubStation.getNodeId());
                List<TrainRouteApiDto> HubToNormal = trainApiClient.getTrainRouteData(hubStation.getNodeId(), inHub.getNodeId());
                resultRoutesList.addAll(NormalToHub);
                resultRoutesList.addAll(HubToNormal);
            }
        }
        log.debug("받은 노선 개수 : {}", resultRoutesList.size());
        return common.processDataListToDB(trainRouteApiMapper, resultRoutesList);
    }
}