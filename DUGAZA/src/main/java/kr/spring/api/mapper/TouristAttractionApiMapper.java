package kr.spring.api.mapper;

import kr.spring.api.dto.TouristAttrationDetailApiDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TouristAttractionApiMapper extends CommonApiMapper{
    void insert(TouristAttrationDetailApiDto touristAttrationDetailApiDto);
    void update(TouristAttrationDetailApiDto touristAttrationDetailApiDto);
}
