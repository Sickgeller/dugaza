package kr.spring.trainsportation.service;

import kr.spring.api.dto.TrainRouteApiDto;
import kr.spring.trainsportation.vo.TrainCityVO;
import kr.spring.trainsportation.vo.TrainStationVO;

import java.util.List;

public interface TrainService {
    
    // 도시 목록 조회
    List<TrainCityVO> getAllCities();
    
    // 도시별 역 목록 조회
    List<TrainStationVO> getStationsByCity(Integer cityCode);
    
    // 노선 검색
    List<TrainRouteApiDto> searchRoutes(String depPlaceId, String arrPlaceId, String depPlandTime);
    
    // 전체 역 목록 조회
    List<TrainStationVO> getAllStations();
} 