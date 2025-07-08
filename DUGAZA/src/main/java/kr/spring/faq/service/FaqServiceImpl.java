package kr.spring.faq.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.spring.faq.dao.FaqMapper;
import kr.spring.faq.vo.FaqVO;

@Service
public class FaqServiceImpl implements FaqService{

	@Autowired
	private FaqMapper faqMapper;

	@Override
	public List<FaqVO> getFaqList() {
		return faqMapper.getFaqList();
	}

}
