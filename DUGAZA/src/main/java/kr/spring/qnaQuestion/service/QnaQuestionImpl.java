package kr.spring.qnaQuestion.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.spring.qnaQuestion.dao.QnaMapper;
import kr.spring.qnaQuestion.vo.QnaQuestionVO;

@Service
public class QnaQuestionImpl implements QnaQuestionService{

	@Autowired
	private QnaMapper mapper;
	
	@Override
	public void insertQuestion(QnaQuestionVO qq) {
		mapper.insertQuestion(qq);
	}

}
