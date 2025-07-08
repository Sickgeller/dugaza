package kr.spring.house.faq.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.spring.house.faq.vo.FaqVO;

@Mapper
public interface FaqMapper {
	List<FaqVO> getFaqList();
}
