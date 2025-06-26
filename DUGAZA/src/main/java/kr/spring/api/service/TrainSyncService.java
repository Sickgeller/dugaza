package kr.spring.api.service;

import java.util.Map;

/**
 * 기차 데이터 동기화 서비스
 */
public interface TrainSyncService {

    /**
     * 기차 종류 데이터를 동기화합니다.
     * @return 동기화된 항목 수
     */
    Map<String, Object> syncTrainKindData();

    /**
     *
     * @return
     */
    Map<String, Object> syncTrainAreaCode();
}
