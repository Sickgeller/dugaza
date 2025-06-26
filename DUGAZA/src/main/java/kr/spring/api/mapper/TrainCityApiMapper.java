package kr.spring.api.mapper;

import kr.spring.api.dto.TrainCityApiDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TrainCityApiMapper {
    int insert(TrainCityApiDto trainCityApiDto);
    int update(TrainCityApiDto trainCityApiDto);
}
