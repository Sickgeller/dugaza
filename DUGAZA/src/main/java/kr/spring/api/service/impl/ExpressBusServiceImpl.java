package kr.spring.api.service.impl;

import kr.spring.aop.LogExecutionTime;
import kr.spring.api.client.ExpressBusApiClient;
import kr.spring.api.dto.ExpressBusCityApiDto;
import kr.spring.api.dto.ExpressBusGradeApiDto;
import kr.spring.api.dto.ExpressBusTerminalApiDto;
import kr.spring.api.mapper.ExpressBusCityApiMapper;
import kr.spring.api.mapper.ExpressBusGradeApiMapper;
import kr.spring.api.mapper.ExpressBusTerminalApiMapper;
import kr.spring.api.service.CommonDataSyncSupportService;
import kr.spring.api.service.ExpressBusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExpressBusServiceImpl implements ExpressBusService {

    private final ExpressBusApiClient expressBusApiClient;
    private final ExpressBusCityApiMapper expressBusCityApiMapper;
    private final ExpressBusTerminalApiMapper expressBusTerminalApiMapper;
    private final ExpressBusGradeApiMapper expressBusGradeApiMapper;
    private final CommonDataSyncSupportService common;

    @Override
    @LogExecutionTime(category = "ExpressBusCitySync")
    public Map<String, Object> syncCityData() {
        List<ExpressBusCityApiDto> areaList = expressBusApiClient.getCityData();
        return common.processDataListToDB(expressBusCityApiMapper, areaList);
    }

    @Override
    @LogExecutionTime(category = "ExpressBusTerminalSync")
    public Map<String, Object> syncTerminalData() {
        List<ExpressBusTerminalApiDto> terminalList = expressBusApiClient.getTerminalData();
        return common.processDataListToDB(expressBusTerminalApiMapper , terminalList);
    }

    @Override
    @LogExecutionTime(category = "ExpressBusGradeSync")
    public Map<String, Object> syncGradeData() {
        List<ExpressBusGradeApiDto> terminalList = expressBusApiClient.getGradeData();
        return common.processDataListToDB(expressBusGradeApiMapper , terminalList);
    }
}
