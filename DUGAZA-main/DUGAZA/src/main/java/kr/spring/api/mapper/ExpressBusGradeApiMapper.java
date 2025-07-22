package kr.spring.api.mapper;

import kr.spring.api.dto.ExpressBusGradeApiDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ExpressBusGradeApiMapper extends CommonApiMapper{
    int insert(ExpressBusGradeApiDto expressBusGradeApiDto);
    int update(ExpressBusGradeApiDto expressBusGradeApiDto);
}
