package kr.spring.faq.service;

import java.util.List;

import kr.spring.faq.vo.FaqVO;

public interface FaqService {
	List<FaqVO> getFaqList();
}
