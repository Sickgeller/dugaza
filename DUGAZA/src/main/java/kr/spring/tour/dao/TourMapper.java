package kr.spring.tour.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import kr.spring.tour.vo.TourVO;

@Mapper
public interface TourMapper {
	public List<TourVO> selectList(Map<String, Object> map);
}
