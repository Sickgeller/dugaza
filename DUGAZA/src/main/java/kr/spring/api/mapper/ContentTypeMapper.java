package kr.spring.api.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ContentTypeMapper {
    
    /**
     * TOUR_CONTENT_CODE 테이블에서 모든 콘텐츠 타입 코드 정보를 조회합니다.
     * @return 콘텐츠 타입 코드 목록 (Map 형태로 category_code와 content_type_code를 포함)
     */
    List<Long> findAllContentTypeCodes();
    
    /**
     * 특정 카테고리 코드에 해당하는 콘텐츠 타입 코드를 조회합니다.
     * @param categoryCode 카테고리 코드
     * @return 콘텐츠 타입 코드 (없으면 null)
     */
    Long findContentTypeCodeByCategoryCode(@Param("categoryCode") String categoryCode);
    
    /**
     * 카테고리 코드의 contentTypeId를 업데이트합니다.
     * @param categoryCode 카테고리 코드
     * @param contentTypeId 콘텐츠 타입 ID
     * @return 업데이트된 행 수
     */
    int updateCategoryContentTypeId(@Param("categoryCode") String categoryCode, 
                                   @Param("contentTypeId") Long contentTypeId);
    
    /**
     * 소분류(code_level=3)인 카테고리 코드 목록을 조회합니다.
     * @return 소분류 카테고리 코드 목록
     */
    List<String> findLevel3CategoryCodes();
} 