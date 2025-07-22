package kr.spring.house.service;

import java.util.List;
import java.util.Map;

import kr.spring.api.client.HouseApiClient;
import kr.spring.api.dto.HouseDetailApiDto;
import kr.spring.api.mapper.HouseApiMapper;
import kr.spring.api.mapper.HouseDetailApiMapper;
import kr.spring.api.service.CommonDataSyncSupportService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.spring.house.dao.HouseMapper;
import kr.spring.house.vo.HouseVO;
@Service
@Transactional
@RequiredArgsConstructor
public class HouseServiceImpl implements HouseService{


	private final HouseApiClient houseApiClient;
	private final CommonDataSyncSupportService commonDataSyncSupportService;
	private final HouseDetailApiMapper houseDetailApiMapper;

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

	@Override
	public void insertWithApi(Long contentId) {
		HouseDetailApiDto houseDetailApiDto = houseApiClient.getHouseDetailData(contentId);
		commonDataSyncSupportService.insertOrUpdate(houseDetailApiMapper, houseDetailApiDto);
	}

}
