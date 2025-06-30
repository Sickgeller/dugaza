package kr.spring.api.mapper;

import kr.spring.api.dto.TrainRouteApiDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TrainRouteApiMapper {
    int insert(TrainRouteApiDto trainStationApiDto);
    int update(TrainRouteApiDto trainStationApiDto);
    List<TrainRouteApiDto> selectAllTrainRoutes();

}
