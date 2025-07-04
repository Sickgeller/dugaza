package kr.spring.house.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import kr.spring.tour.vo.TourVO;

@Mapper
public interface HouseMapper {
	// 숙소 총 개수
	public Integer selectRowCount();
	// 숙소 목록
	public List<TourVO> selectList(Map<String,Object> map);
}
