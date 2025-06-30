package kr.spring.api.mapper;

import kr.spring.api.dto.ExpressBusCityApiDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ExpressBusCityApiMapper {
    int insertCity(ExpressBusCityApiDto expressBusCityApiDto);
    int updateCity(ExpressBusCityApiDto expressBusCityApiDto);
}
