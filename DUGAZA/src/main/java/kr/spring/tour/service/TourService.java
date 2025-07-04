package kr.spring.tour.service;

import java.util.List;
import java.util.Map;

import kr.spring.tour.vo.TourVO;

public interface TourService {
	public Integer selectRowCount();
	public List<TourVO> selectList(Map<String, Object> map);
}
