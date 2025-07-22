package kr.spring.api.service;

import java.util.Map;

/**
 * 숙소 데이터 동기화 서비스
 */
public interface HouseDataSyncService {

    /**
     * 숙소 데이터를 동기화합니다.
     *
     * @return 동기화된 항목 수
     */
    Map<String, Object> syncHouseData();
    
    /**
     * 유효하지 않은 숙소 데이터를 삭제합니다.
     * @return 삭제된 항목 수
     */
//    int cleanInvalidHouseData();
}
