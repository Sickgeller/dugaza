package kr.spring.qnaQuestion.service;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import kr.spring.qnaQuestion.vo.QnaQuestionVO;
import kr.spring.qnaResponse.vo.QnaResponseVO;

public interface QnaQuestionService {
	void insertQuestion(QnaQuestionVO qq);
	List<QnaQuestionVO> getQnaList();
	List<QnaQuestionVO> getQnaListByMember(Long member_id);
	QnaQuestionVO getQnaDetail(Long qna_id);
	void insertAnswer(QnaResponseVO response);
	List<QnaQuestionVO> getAllQnaList();
	void insertResponse(QnaResponseVO responseVO);
	void updateIsAnswered(Long qna_id, String is_answered);
	QnaResponseVO getAnswerByQnaId(Long qnaId);
	QnaQuestionVO getQnaById(Long qnaId);
	void updateResponse(QnaResponseVO responseVO);
}
