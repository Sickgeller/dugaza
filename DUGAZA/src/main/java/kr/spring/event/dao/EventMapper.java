package kr.spring.event.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import kr.spring.event.vo.EventVO;
import kr.spring.tour.vo.TourVO;

@Mapper
public interface EventMapper {
	// 행사 총 개수
	public Integer selectRowCount();
	// 행사 목록
	public List<TourVO> selectList(Map<String,Object> map);
	public EventVO selectEvent(Long id);
	// tour_content에서 기본 정보만 가져오기
	TourVO selectTourContent(Long contentId);
}
