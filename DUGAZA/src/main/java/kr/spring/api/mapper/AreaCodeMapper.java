package kr.spring.api.mapper;

import kr.spring.api.dto.AreaCodeApiDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AreaCodeMapper extends CommonApiMapper{

    AreaCodeApiDto findByAreaCode(@Param("areaCode") String areaCode);

    void insert(AreaCodeApiDto areaCodeApiDto);

    void update(AreaCodeApiDto areaCodeApiDto);
}
