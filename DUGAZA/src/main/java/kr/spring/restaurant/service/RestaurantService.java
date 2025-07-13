package kr.spring.restaurant.service;

import java.util.List;
import java.util.Map;

import kr.spring.restaurant.vo.RestaurantVO;
import kr.spring.tour.vo.TourVO;

public interface RestaurantService {
	// 음식점 총 개수
	public Integer selectRowCount();
	// 음식점 목록
	public List<TourVO> selectList(Map<String,Object> map);
	public RestaurantVO selectRestaurant(Long id);
	RestaurantVO selectRestaurantWithApi(Long contentId);
}
