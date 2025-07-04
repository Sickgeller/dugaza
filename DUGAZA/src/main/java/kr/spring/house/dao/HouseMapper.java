package kr.spring.house.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import kr.spring.house.vo.HouseVO;

@Mapper
public interface HouseMapper {
	// 숙소 목록
	public List<HouseVO> selectList(Map<String,Object> map);
}
