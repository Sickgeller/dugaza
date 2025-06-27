package kr.spring.api.service;

import java.util.Map;

public interface ExpressBusService {

    public Map<String,Object> syncCityData();

    public Map<String, Object> syncTerminalData();
}
