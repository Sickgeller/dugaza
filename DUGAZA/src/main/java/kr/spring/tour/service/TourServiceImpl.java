package kr.spring.tour.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.spring.tour.dao.TourMapper;
import kr.spring.tour.vo.TourVO;

@Service
@Transactional
public class TourServiceImpl implements TourService{
	
	@Autowired
	private TourMapper tourMapper;
	
	@Override
	public Integer selectRowCount() {
		return tourMapper.selectRowCount();
	}

	@Override
	public List<TourVO> selectList(Map<String, Object> map) {
		return tourMapper.selectList(map);
	}

	@Override
	public TourVO selectTourContent(Long contentId) {
		return tourMapper.selectTourContent(contentId);
	}

}
