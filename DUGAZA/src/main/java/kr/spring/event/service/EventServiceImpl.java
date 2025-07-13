package kr.spring.event.service;

import java.util.List;
import java.util.Map;

import kr.spring.api.client.EventApiClient;
import kr.spring.api.dto.EventDetailApiDto;
import kr.spring.api.mapper.EventDetailApiMapper;
import kr.spring.api.service.CommonDataSyncSupportService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.spring.event.dao.EventMapper;
import kr.spring.event.vo.EventVO;
import kr.spring.tour.vo.TourVO;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class EventServiceImpl implements EventService{
	
	private final EventMapper eventMapper;
	private final EventApiClient eventApiClient;
	private final CommonDataSyncSupportService commonDataSyncSupportService;
	private final EventDetailApiMapper eventDetailApiMapper;

	@Override
	public Integer selectRowCount() {
		return eventMapper.selectRowCount();
	}

	@Override
	public List<TourVO> selectList(Map<String, Object> map) {
		return eventMapper.selectList(map);
	}

	@Override
	public EventVO selectEvent(Long id) {
		return eventMapper.selectEvent(id);
	}

	@Override
	public EventVO selectEventWithApi(Long contentId) {
		EventDetailApiDto eventDetailApiDto = eventApiClient.getEventDetailData(contentId);
		if(eventDetailApiDto.getContentId() != null) {
			commonDataSyncSupportService.insertOrUpdate(eventDetailApiMapper, eventDetailApiDto);
        	return eventMapper.selectEvent(contentId);
		}else {
			return null;
		}
	}

	@Override
	public TourVO selectTourContent(Long contentId) {
		return eventMapper.selectTourContent(contentId);
	}

}
