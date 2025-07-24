package kr.spring.event.service;

import java.util.List;
import java.util.Map;

import kr.spring.event.vo.EventVO;
import kr.spring.tour.vo.TourVO;

public interface EventService {
	// 행사 총 개수
	public Integer selectRowCount();
	// 행사 목록
	public List<TourVO> selectList(Map<String,Object> map);
	public EventVO selectEvent(Long id);
    EventVO selectEventWithApi(Long contentId);
    // tour_content에서 기본 정보만 가져오기
    TourVO selectTourContent(Long contentId);
}
