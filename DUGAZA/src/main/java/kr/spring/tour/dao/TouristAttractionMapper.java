package kr.spring.tour.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import kr.spring.tour.vo.TouristAttractionVO;

@Mapper
public interface TouristAttractionMapper {
	public TouristAttractionVO selectAttraction(Long id);
}
