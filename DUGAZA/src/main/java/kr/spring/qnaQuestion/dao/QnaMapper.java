package kr.spring.qnaQuestion.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.spring.qnaQuestion.vo.QnaQuestionVO;
import kr.spring.qnaResponse.vo.QnaResponseVO;

@Mapper
public interface QnaMapper {
	void insertQuestion(QnaQuestionVO qq);
	List<QnaQuestionVO> getQnaList();
	List<QnaQuestionVO> getQnaListByMember(Long memberId);
	QnaQuestionVO getQnaDetail(Long qna_id);
	List<QnaQuestionVO> getAllQnaList();
	void insertAnswer();
	void insertResponse();
	void updateIsAnswered(@Param("qna_id") Long qna_id, @Param("is_answered") String is_answered);
	QnaResponseVO getAnswerByQnaId(Long qnaId);
	QnaQuestionVO getQnaById(Long qnaId);
	void updateResponse(QnaResponseVO responseVO);
}

