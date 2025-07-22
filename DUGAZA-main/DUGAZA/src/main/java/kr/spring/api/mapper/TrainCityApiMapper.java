package kr.spring.api.mapper;

import kr.spring.api.dto.TrainCityApiDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TrainCityApiMapper extends CommonApiMapper{
    int insert(TrainCityApiDto trainCityApiDto);
    int update(TrainCityApiDto trainCityApiDto);
    List<TrainCityApiDto> getAllCityDto();
}
