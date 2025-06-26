package kr.spring.api.mapper;

import kr.spring.api.dto.TrainStationApiDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TrainStationApiMapper {
    int insert(TrainStationApiDto trainStationApiDto);
    int update(TrainStationApiDto trainStationApiDto);
    List<TrainStationApiDto> getAllStation();
}
