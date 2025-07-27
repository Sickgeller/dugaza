package kr.spring.trainsportation.service.impl;

import kr.spring.api.client.TrainApiClient;
import kr.spring.api.dto.TrainRouteApiDto;
import kr.spring.trainsportation.dao.TrainMapper;
import kr.spring.trainsportation.service.TrainService;
import kr.spring.trainsportation.vo.TrainCityVO;
import kr.spring.trainsportation.vo.TrainStationVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainServiceImpl implements TrainService {

    private final TrainMapper trainMapper;
    private final TrainApiClient trainApiClient;

    @Override
    public List<TrainCityVO> getAllCities() {
        log.info("기차 도시 목록 조회");
        return trainMapper.selectAllCities();
    }

    @Override
    public List<TrainStationVO> getStationsByCity(Integer cityCode) {
        log.info("도시별 기차역 목록 조회: cityCode={}", cityCode);
        return trainMapper.selectStationsByCity(cityCode);
    }

    @Override
    public List<TrainRouteApiDto> searchRoutes(String depPlaceId, String arrPlaceId, String depPlandTime) {
        return trainApiClient.getTrainRouteData(depPlaceId, arrPlaceId, depPlandTime);
    }

    @Override
    public List<TrainStationVO> getAllStations() {
        log.info("전체 기차역 목록 조회");
        return trainMapper.selectAllStations();
    }
} 