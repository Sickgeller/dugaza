package kr.spring.house.service;

import java.util.List;
import java.util.Map;

import kr.spring.content.vo.ContentVO;

public interface HouseService {
	// 숙소 총 개수
	public Integer selectRowCount();
	// 숙소 목록
	public List<ContentVO> selectList(Map<String, Object> map);
}
