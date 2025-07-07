package kr.spring.tour.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.spring.tour.dao.TouristAttractionMapper;
import kr.spring.tour.vo.TouristAttractionVO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class TouristAttractionServiceImpl implements TouristAttractionService{
	@Autowired
	private TouristAttractionMapper touristAttractionMapper;

	public TouristAttractionVO selectAttraction(Long id) {
		return touristAttractionMapper.selectAttraction(id);
	}

}
