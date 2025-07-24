package kr.spring.api.mapper;

import kr.spring.api.dto.EventDetailApiDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EventDetailApiMapper extends CommonApiMapper{
    void insert(EventDetailApiDto dto);
    void update(EventDetailApiDto dto);
}
