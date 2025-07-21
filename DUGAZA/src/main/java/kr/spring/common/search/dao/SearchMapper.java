package kr.spring.common.search.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Select;

import kr.spring.common.search.vo.SearchResultVO;
import kr.spring.tour.vo.TourVO;

public interface SearchMapper {
	public List<TourVO> integratedSearch(String keyword);
	public List<Map<String, Object>> countSearchGroupedByCategory(String keyword);
}
