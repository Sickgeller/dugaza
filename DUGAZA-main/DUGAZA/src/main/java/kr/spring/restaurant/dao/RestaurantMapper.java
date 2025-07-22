package kr.spring.restaurant.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import kr.spring.restaurant.vo.RestaurantVO;
import kr.spring.tour.vo.TourVO;

@Mapper
public interface RestaurantMapper {
	// 음식점 총 개수
	public Integer selectRowCount();
	// 음식점 목록
	public List<TourVO> selectList(Map<String,Object> map);
	public RestaurantVO selectRestaurant(Long id);
}
