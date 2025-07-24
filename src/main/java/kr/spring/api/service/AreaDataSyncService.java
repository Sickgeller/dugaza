package kr.spring.api.service;

import java.util.Map;

public interface AreaDataSyncService {
    public Map<String, Object> syncAreaCodes();
    public Map<String, Object> syncSigunguCodes();
}
