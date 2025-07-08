package kr.spring.qnaQuestion.dao;

import org.apache.ibatis.annotations.Mapper;

import kr.spring.qnaQuestion.vo.QnaQuestionVO;

@Mapper
public interface QnaMapper {
	void insertQuestion(QnaQuestionVO qq);
}
