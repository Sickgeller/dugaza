package kr.spring.api.mapper;

import com.project.dugaza.api.dto.HouseApiDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface HouseApiMapper {
    void insert(HouseApiDto houseApiDto);
    int deleteInvalidHouseData();
}
