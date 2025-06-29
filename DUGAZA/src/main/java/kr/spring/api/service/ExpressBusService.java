package kr.spring.api.service;

import java.util.Map;

public interface ExpressBusService {

    Map<String,Object> syncCityData();

    Map<String, Object> syncTerminalData();

    Map<String, Object> syncGradeData();
}
