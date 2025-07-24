package kr.spring.common.search.service;

import java.util.List;
import java.util.Map;

import kr.spring.tour.vo.TourVO;

public interface SearchService {
	public List<TourVO> integratedSearch(String keyword);
	public List<Map<String, Object>> countSearchGroupedByCategory(String keyword);
}
