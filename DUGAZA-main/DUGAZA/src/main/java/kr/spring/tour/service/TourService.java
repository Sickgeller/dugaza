package kr.spring.tour.service;

import java.util.List;
import java.util.Map;

import kr.spring.tour.vo.TourVO;

public interface TourService {
	public Integer selectRowCount();
	public List<TourVO> selectList(Map<String, Object> map);
	// 아이디로부터 컨텐츠 타입을 읽어와 해당 카테고리로 처리 넘기기 위함
	public Integer selectContentTypeId(Long id);
}
