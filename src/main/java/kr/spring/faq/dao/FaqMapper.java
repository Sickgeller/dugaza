package kr.spring.faq.dao;

import java.util.List;

import kr.spring.faq.vo.FaqVO;

//@Mapper
public interface FaqMapper {
	List<FaqVO> getFaqList();
}
