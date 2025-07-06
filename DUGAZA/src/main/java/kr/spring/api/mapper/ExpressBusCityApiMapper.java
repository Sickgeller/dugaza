package kr.spring.api.mapper;

import kr.spring.api.dto.ExpressBusCityApiDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ExpressBusCityApiMapper extends CommonApiMapper{
    int insert(ExpressBusCityApiDto expressBusCityApiDto);
    int update(ExpressBusCityApiDto expressBusCityApiDto);
}
