package kr.spring.api.service;

import java.util.Map;

public interface EventDataSyncService {
    public Map<String, Object> syncEventCode(Long startYear);
}
