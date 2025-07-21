package kr.spring.trainsportation.dao;

import kr.spring.trainsportation.vo.TrainCityVO;
import kr.spring.trainsportation.vo.TrainRouteVO;
import kr.spring.trainsportation.vo.TrainStationVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface TrainMapper {
    
    // 도시 목록 조회
    List<TrainCityVO> selectAllCities();
    
    // 도시별 역 목록 조회
    List<TrainStationVO> selectStationsByCity(Integer cityCode);
    
    // 노선 검색 (출발지=도착지)
    List<TrainRouteVO> searchRoutes(Map<String, Object> params);
    
    // 전체 역 목록 조회
    List<TrainStationVO> selectAllStations();
} 