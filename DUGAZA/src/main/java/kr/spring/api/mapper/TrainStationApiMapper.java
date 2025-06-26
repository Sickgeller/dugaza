package kr.spring.api.mapper;

import kr.spring.api.dto.TrainStationApiDto;

public interface TrainStationApiMapper {
    int insert(TrainStationApiDto trainStationApiDto);
    int update(TrainStationApiDto trainStationApiDto);
}
