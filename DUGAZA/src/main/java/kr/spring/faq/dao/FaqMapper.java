package kr.spring.faq.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.spring.faq.vo.FaqVO;

@Mapper
public interface FaqMapper {
	List<FaqVO> getFaqList();
}
