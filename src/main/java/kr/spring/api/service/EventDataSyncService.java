package kr.spring.api.service;

import java.util.Map;

/**
 * 이벤트 데이터 동기화 서비스
 */
public interface EventDataSyncService {

    /**
     * 이벤트 데이터를 동기화합니다.
     *
     * @param startYear 조회 시작 연도
     * @return 동기화된 항목 수
     */
    Map<String, Object> syncEventData(Long startYear);
    


    Map<String, Object> syncEventDateData (Long startYear);
}
