package kr.spring.common.search.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.spring.common.search.dao.SearchMapper;
import kr.spring.tour.vo.TourVO;
@Service
@Transactional
public class SearchServiceImpl implements SearchService{
	
	@Autowired
	private SearchMapper searchMapper;

	@Override
	public List<TourVO> integratedSearch(String keyword) {
		return searchMapper.integratedSearch(keyword);
	}

	@Override
	public List<Map<String, Object>> countSearchGroupedByCategory(String keyword) {
		return searchMapper.countSearchGroupedByCategory(keyword);
	}

}
