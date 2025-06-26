package kr.spring.api.mapper;

import com.project.dugaza.api.dto.SigunguCodeApiDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SigunguCodeMapper {

    SigunguCodeApiDto findBySigunguCode(@Param("sigunguCode") String sigunguCode);
    
    SigunguCodeApiDto findBySigunguCodeAndParentCode(
        @Param("sigunguCode") String sigunguCode, 
        @Param("parentCode") String parentCode
    );
    
    List<SigunguCodeApiDto> findByParentCode(@Param("parentCode") String parentCode);

    void insert(SigunguCodeApiDto sigunguCodeApiDto);

    void update(SigunguCodeApiDto sigunguCodeApiDto);
    
    void deleteAll();
} 