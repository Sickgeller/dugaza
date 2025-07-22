package kr.spring.tour.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import kr.spring.tour.vo.TourVO;

@Mapper
public interface TourMapper {
	public Integer selectRowCount();
	public List<TourVO> selectList(Map<String, Object> map);
	// 아이디로부터 컨텐츠 타입을 읽어와 해당 카테고리로 처리 넘기기 위함
	@Select("SELECT content_type_id FROM tour_content WHERE content_id=#{id}")
	public Integer selectContentTypeId(Long id);
}
