package kr.spring.trainsportation.service.impl;

import kr.spring.trainsportation.dao.TrainMapper;
import kr.spring.trainsportation.service.TrainService;
import kr.spring.trainsportation.vo.TrainCityVO;
import kr.spring.trainsportation.vo.TrainRouteVO;
import kr.spring.trainsportation.vo.TrainStationVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainServiceImpl implements TrainService {

    private final TrainMapper trainMapper;

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
    public List<TrainRouteVO> searchRoutes(Map<String, Object> params) {
        log.info("기차 노선 검색: params={}", params);
        return trainMapper.searchRoutes(params);
    }

    @Override
    public List<TrainStationVO> getAllStations() {
        log.info("전체 기차역 목록 조회");
        return trainMapper.selectAllStations();
    }
} 