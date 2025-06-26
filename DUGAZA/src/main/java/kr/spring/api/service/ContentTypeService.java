package kr.spring.api.service;

import java.util.Map;

/**
 * 콘텐츠 타입 관리 서비스
 */
public interface ContentTypeService {

    /**
     * 소분류(code_level=3) 카테고리의 contentTypeId를 TOUR_CONTENT_CODE 테이블의 값으로 업데이트합니다.
     * @return 업데이트된 카테고리 수와 실패한 카테고리 수가 포함된 결과 맵
     */
    Map<String, Integer> updateContentTypeForLevel3Categories();
    
    /**
     * 특정 카테고리 코드의 contentTypeId를 업데이트합니다.
     * @param categoryCode 카테고리 코드
     * @param contentTypeId 콘텐츠 타입 ID
     * @return 업데이트 성공 여부
     */
    boolean updateContentTypeForCategory(String categoryCode, Long contentTypeId);
} 