package kr.spring.restaurant.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.spring.restaurant.dao.RestaurantMapper;
import kr.spring.restaurant.vo.RestaurantVO;
import kr.spring.tour.vo.TourVO;
@Service
@Transactional
public class RestaurantServiceImpl implements RestaurantService{

	@Autowired
	private RestaurantMapper restaurantMapper;
	
	@Override
	public Integer selectRowCount() {
		return restaurantMapper.selectRowCount();
	}

	@Override
	public List<TourVO> selectList(Map<String, Object> map) {
		return restaurantMapper.selectList(map);
	}

	@Override
	public RestaurantVO selectRestaurant(Long id) {
		return restaurantMapper.selectRestaurant(id);
	}

}
