package kr.spring.api.service.impl;

import kr.spring.aop.LogExecutionTime;
import kr.spring.api.client.EventApiClient;
import kr.spring.api.dto.EventContentApiDto;
import kr.spring.api.dto.EventDetailApiDto;
import kr.spring.api.mapper.EventContentMapper;
import kr.spring.api.mapper.EventDetailApiMapper;
import kr.spring.api.service.CommonDataSyncSupportService;
import kr.spring.api.service.EventDataSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventDataSyncServiceImpl implements EventDataSyncService {

    private final EventApiClient eventApiClient;
    private final EventContentMapper eventContentMapper;
    private final EventDetailApiMapper eventDetailApiMapper;
    private final CommonDataSyncSupportService common;

    @Override
    @Transactional
    @LogExecutionTime(category = "EventSync")
    public Map<String, Object> syncEventData(Long startYear) {
        List<EventContentApiDto> eventContentList = eventApiClient.getEventContent(startYear);
        return common.processDataListToDB(eventContentMapper, eventContentList);
    }

    @Override
    @Transactional
    @LogExecutionTime(category = "EventSync")
    public Map<String, Object> syncEventDateData(Long startYear) {
        List<EventDetailApiDto> list = eventApiClient.getEventContent2(startYear);
        return common.processDataListToDB(eventDetailApiMapper , list);
    }
}


