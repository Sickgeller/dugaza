package kr.spring.api.mapper;

import kr.spring.api.dto.CategoryCodeApiDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CategoryCodeMapper {

    /**
     * 카테고리 코드로 카테고리 정보를 조회합니다.
     * @param categoryCode 카테고리 코드
     * @return 카테고리 정보
     */
    CategoryCodeApiDto findByCategoryCode(@Param("categoryCode") String categoryCode);

    /**
     * 카테고리 코드를 저장합니다.
     * @param categoryCodeApiDto 카테고리 정보
     * @return 저장된 행 수
     */
    int insertCategoryCode(CategoryCodeApiDto categoryCodeApiDto);

    /**
     * 카테고리 코드를 업데이트합니다.
     * @param categoryCodeApiDto 카테고리 정보
     * @return 업데이트된 행 수
     */
    int updateCategoryCode(CategoryCodeApiDto categoryCodeApiDto);

    /**
     * 모든 카테고리 코드를 조회합니다.
     * @return 카테고리 코드 목록
     */
    List<CategoryCodeApiDto> findAllCategoryCodes();

    /**
     * 부모 코드로 하위 카테고리 목록을 조회합니다.
     * @param parentCode 부모 카테고리 코드
     * @return 하위 카테고리 목록
     */
    List<CategoryCodeApiDto> findByParentCode(@Param("parentCode") String parentCode);

    /**
     * 코드 레벨로 카테고리 목록을 조회합니다.
     * @param codeLevel 코드 레벨 (1: 대분류, 2: 중분류, 3: 소분류)
     * @return 해당 레벨의 카테고리 목록
     */
    List<CategoryCodeApiDto> findByCodeLevel(@Param("codeLevel") Integer codeLevel);
}
