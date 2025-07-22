package kr.spring.tour.service;

import kr.spring.tour.vo.TouristAttractionVO;

public interface TouristAttractionService {
	public TouristAttractionVO selectAttraction(Long id);
}
