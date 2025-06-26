package kr.spring.api.mapper;

import kr.spring.api.dto.HouseApiDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface HouseApiMapper {
    void insert(HouseApiDto houseApiDto);
    int deleteInvalidHouseData();
}
