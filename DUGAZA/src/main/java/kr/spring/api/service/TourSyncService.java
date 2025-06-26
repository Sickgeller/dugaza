package kr.spring.api.service;

import com.project.dugaza.common.ContentTypeid;

import java.util.Map;

public interface TourSyncService {
    Map<String,Object> getAllTourData();

    Map<String,Object> getTouristData(ContentTypeid contentTypeId);
}
