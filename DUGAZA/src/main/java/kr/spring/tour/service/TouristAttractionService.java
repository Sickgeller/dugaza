package kr.spring.tour.service;

import kr.spring.tour.vo.TouristAttractionVO;
import kr.spring.tour.vo.TourVO;

public interface TouristAttractionService {
	public TouristAttractionVO selectAttraction(Long id);
	TouristAttractionVO selectAttractionWithApi(Long contentId);
}
