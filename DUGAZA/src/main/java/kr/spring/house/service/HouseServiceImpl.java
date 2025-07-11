package kr.spring.house.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.spring.house.dao.HouseMapper;
import kr.spring.house.vo.HouseVO;
@Service
@Transactional
public class HouseServiceImpl implements HouseService{

	@Autowired
	private HouseMapper houseMapper;
	
	@Override
	public List<HouseVO> selectList(Map<String, Object> map) {
		return houseMapper.selectList(map);
	}

	@Override
	public Integer selectRowCount(Map<String, Object> map) {
		return houseMapper.selectRowCount(map);
	}

	@Override
	public HouseVO selectHouse(Long id) {
		return houseMapper.selectHouse(id);
	}

	@Override
	public HouseVO selectHouseWithSellerId(Long sellerId) {
		return houseMapper.selectHouseWithSellerId(sellerId);
	}

}
