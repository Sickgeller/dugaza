package kr.spring.api.service;

import java.util.Map;

public interface TrainSyncService {

    /**
     * 차량종류, 차량이름 조회 및 동기화
     * @return 조회 및 동기화 성공 실패 총합 수
     */
    Map<String, Object> syncTrainKindData();

    /**
     *
     * @return
     */
    Map<String, Object> syncTrainAreaCode();
}
