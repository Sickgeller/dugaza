package kr.spring.api.mapper;

import kr.spring.api.dto.HouseDetailApiDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface HouseDetailApiMapper extends CommonApiMapper{
    void insert(HouseDetailApiDto houseDetailApiDto);
    void update(HouseDetailApiDto houseDetailApiDto);
}
