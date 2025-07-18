package kr.spring.qnaQuestion.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.spring.qnaQuestion.dao.QnaMapper;
import kr.spring.qnaQuestion.vo.QnaQuestionVO;
import kr.spring.qnaResponse.vo.QnaResponseVO;

@Service
public class QnaQuestionImpl implements QnaQuestionService{

	@Autowired
	private QnaMapper mapper;
	
	@Override
	public void insertQuestion(QnaQuestionVO qq) {
		mapper.insertQuestion(qq);
	}

	@Override
	public List<QnaQuestionVO> getQnaList() {
		return mapper.getQnaList();
	}

	@Override
	public List<QnaQuestionVO> getQnaListByMember(Long member_id) {
		return mapper.getQnaListByMember(member_id);
	}

	@Override
	public QnaQuestionVO getQnaDetail(Long qna_id) {
		return mapper.getQnaDetail(qna_id);
	}

	@Override
	@Transactional
	public void insertAnswer(QnaResponseVO response) {
		mapper.insertAnswer();
	}

	@Override
	public QnaResponseVO getAnswerByQnaId(Long qna_id) {
		return mapper.getAnswerByQnaId(qna_id);
	}

	@Override
	public List<QnaQuestionVO> getAllQnaList() {
		return mapper.getAllQnaList();
	}

	@Override
	public void insertResponse(QnaResponseVO responseVO) {
		mapper.insertResponse();
	}



	@Override
	public QnaQuestionVO getQnaById(Long qnaId) {
		return mapper.getQnaById(qnaId);
	}

	@Override
	public void updateResponse(QnaResponseVO responseVO) {
		mapper.updateResponse(responseVO);
	}

	@Override
	public void updateIsAnswered(Long qna_id, String is_answered) {
		mapper.updateIsAnswered(qna_id, is_answered);
	}



}
