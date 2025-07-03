package kr.spring.tour.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import kr.spring.content.vo.ContentVO;

@Mapper
public interface TourMapper {
	public List<ContentVO> selectList(Map<String, Object> map);
}
