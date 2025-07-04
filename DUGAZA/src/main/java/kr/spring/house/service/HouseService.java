package kr.spring.house.service;

import java.util.List;
import java.util.Map;

import kr.spring.house.vo.HouseVO;

public interface HouseService {
	// 숙소 목록
	public List<HouseVO> selectList(Map<String, Object> map);
}
