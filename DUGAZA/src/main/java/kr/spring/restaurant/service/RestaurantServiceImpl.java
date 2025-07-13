package kr.spring.restaurant.service;

import java.util.List;
import java.util.Map;

import kr.spring.api.client.RestaurantDetailAplClient;
import kr.spring.api.dto.RestaurantDetailApiDto;
import kr.spring.api.mapper.RestaurantDetailApiMapper;
import kr.spring.api.service.CommonDataSyncSupportService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.spring.restaurant.dao.RestaurantMapper;
import kr.spring.restaurant.vo.RestaurantVO;
import kr.spring.tour.vo.TourVO;
@Service
@Transactional
@RequiredArgsConstructor
public class
RestaurantServiceImpl implements RestaurantService{

	private final RestaurantMapper restaurantMapper;
	private final RestaurantDetailAplClient restaurantDetailAplClient;
	private final RestaurantDetailApiMapper restaurantDetailApiMapper;
	private final CommonDataSyncSupportService commonDataSyncSupportService;

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

	@Override
	public RestaurantVO selectRestaurantWithApi(Long contentId) {
		RestaurantVO result;
		RestaurantDetailApiDto dto = null;
		dto = restaurantDetailAplClient.getRestaurantDetailData(contentId);
		commonDataSyncSupportService.insertOrUpdate(restaurantDetailApiMapper,dto);
		result = selectRestaurant(contentId);
		return result;
	}



}
