package kr.spring.api.service;

/**
 * 카테고리 코드 데이터 동기화 서비스
 */
public interface CategoryDataSyncService {

    /**
     * 모든 카테고리 코드 데이터를 동기화합니다.
     * 대분류, 중분류, 소분류 코드를 모두 가져와 저장합니다.
     * @return 동기화된 총 항목 수
     */
    int syncAllCategoryCodes();
    
    /**
     * 대분류 카테고리 코드를 동기화합니다.
     * @return 동기화된 항목 수
     */
    int syncCategoryCode1();
    
    /**
     * 중분류 카테고리 코드를 동기화합니다.
     * @return 동기화된 항목 수
     */
    int syncCategoryCode2();
    
    /**
     * 소분류 카테고리 코드를 동기화합니다.
     * @return 동기화된 항목 수
     */
    int syncCategoryCode3();

//    void syncCategoryCodes();
}