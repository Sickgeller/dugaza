package kr.spring.api.service;

import kr.spring.common.ContentTypeid;

import java.util.Map;

/**
 * 관광 데이터 동기화 서비스
 */
public interface TourSyncService {

//    int syncTouristData(ContentTypeid contentTypeId);

    Map<String,Object> getAllTourData();

    Map<String,Object> getTouristData(ContentTypeid contentTypeId);

    Map<String, Object> updateTourData();

}
