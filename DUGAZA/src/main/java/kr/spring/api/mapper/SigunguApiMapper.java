package kr.spring.api.mapper;

import kr.spring.api.dto.SigunguCodeApiDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SigunguApiMapper extends CommonApiMapper{

    SigunguCodeApiDto findBySigunguCode(@Param("sigunguCode") String sigunguCode);
    
    SigunguCodeApiDto findBySigunguCodeAndAreaCode(
        @Param("sigunguCode") String sigunguCode, 
        @Param("areaCode") String areaCode
    );
    
    List<SigunguCodeApiDto> findByAreaCode(@Param("areaCode") String areaCode);

    void insert(SigunguCodeApiDto sigunguCodeApiDto);

    void update(SigunguCodeApiDto sigunguCodeApiDto);
    
    void deleteAll();
} 