package kr.spring.api.mapper;

import kr.spring.api.dto.HouseApiDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface HouseApiMapper extends CommonApiMapper{
    void insert(HouseApiDto houseApiDto);
    void update(HouseApiDto houseApiDto);
    int deleteInvalidHouseData();
}
