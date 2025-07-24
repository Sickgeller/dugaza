package kr.spring.tour.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import kr.spring.tour.vo.CulturalCenterVO;

@Mapper
public interface CulturalCenterMapper {
	@Select("SELECT * FROM cultural_center_detail WHERE content_id=#{id}")
	public CulturalCenterVO selectCenter(Long id);
}
