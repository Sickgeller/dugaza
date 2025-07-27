package kr.spring.tour.service;

import java.util.List;
import java.util.Map;

import kr.spring.tour.vo.TourVO;
import kr.spring.util.CursorPagingUtil;

public interface TourService {
	public Integer selectRowCount();
	public List<TourVO> selectList(Map<String, Object> map);
	
	// 커서 기반 페이지네이션을 위한 메서드들
	public List<TourVO> selectListByCursor(CursorPagingUtil cursorPaging);
	public boolean hasNextPage(CursorPagingUtil cursorPaging);
	
	// tour_content에서 기본 정보만 가져오기
	TourVO selectTourContent(Long contentId);
}
