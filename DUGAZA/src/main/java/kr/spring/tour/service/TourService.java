package kr.spring.tour.service;

import java.util.List;
import java.util.Map;

import kr.spring.tour.vo.TourVO;

public interface TourService {
	public Integer selectRowCount();
	public List<TourVO> selectList(Map<String, Object> map);
	// tour_content에서 기본 정보만 가져오기
	TourVO selectTourContent(Long contentId);
}
