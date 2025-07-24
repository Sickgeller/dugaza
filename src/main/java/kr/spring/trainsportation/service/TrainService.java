package kr.spring.trainsportation.service;

import kr.spring.trainsportation.vo.TrainCityVO;
import kr.spring.trainsportation.vo.TrainRouteVO;
import kr.spring.trainsportation.vo.TrainStationVO;

import java.util.List;
import java.util.Map;

public interface TrainService {
    
    // 도시 목록 조회
    List<TrainCityVO> getAllCities();
    
    // 도시별 역 목록 조회
    List<TrainStationVO> getStationsByCity(Integer cityCode);
    
    // 노선 검색
    List<TrainRouteVO> searchRoutes(Map<String, Object> params);
    
    // 전체 역 목록 조회
    List<TrainStationVO> getAllStations();
} 