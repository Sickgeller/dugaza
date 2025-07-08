package kr.spring.event.service;

import java.util.List;
import java.util.Map;

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
public class EventServiceImpl implements EventService{
	
	@Autowired
	private EventMapper eventMapper;
	
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

}
