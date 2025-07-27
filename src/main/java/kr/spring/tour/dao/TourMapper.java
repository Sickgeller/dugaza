package kr.spring.tour.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import kr.spring.tour.vo.TourVO;

@Mapper
public interface TourMapper {
	public Integer selectRowCount();
	public List<TourVO> selectList(Map<String, Object> map);
	
	// 커서 기반 페이지네이션을 위한 메서드들
	public List<TourVO> selectListByCursor(Map<String, Object> map);
	public boolean hasNextPage(Map<String, Object> map);
	
	// tour_content에서 기본 정보만 가져오기
	TourVO selectTourContent(Long contentId);
}
