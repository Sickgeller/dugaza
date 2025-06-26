package kr.spring.api.mapper;

import kr.spring.api.dto.TourApiDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TourApiMapper {
    void insert(TourApiDto tourApiDto);
}
