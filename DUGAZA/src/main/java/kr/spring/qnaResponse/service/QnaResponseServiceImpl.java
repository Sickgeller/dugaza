package kr.spring.qnaResponse.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.spring.qnaQuestion.dao.QnaMapper;
import kr.spring.qnaQuestion.vo.QnaQuestionVO;
import kr.spring.qnaResponse.dao.QnaResponseMapper;
import kr.spring.qnaResponse.vo.QnaResponseVO;

@Service
public class QnaResponseServiceImpl implements QnaResponseService{

	@Autowired
	private QnaResponseMapper mapper;  
	
	@Autowired
	private QnaMapper qnaMapper; // ★ 추가
	
	@Override
	public void insertResponse(QnaResponseVO responseVO) {
		mapper.insertResponse(responseVO);
		
	}

	@Override
	public QnaResponseVO getAnswerByQnaId(Long qna_id) {
		return mapper.getAnswerByQnaId(qna_id);
	}

	@Override
	public List<QnaQuestionVO> getAllQnaList() {
		return qnaMapper.getAllQnaList();
	}

	@Override
	public QnaQuestionVO getQnaById(Long qnaId) {
		return mapper.getQnaById(qnaId);
	}



	@Override
	public void updateIsAnswered(Long qna_id, String is_answered) {
		mapper.updateIsAnswered(qna_id, is_answered);
		
	}

	@Override
	public void updateResponse(QnaResponseVO responseVO) {
		mapper.updateResponse(responseVO); // 답변 내용 수정
		mapper.updateIsAnswered(responseVO.getQna_id(), "Y"); // 상태도 "Y"로 바꿈
	}


}
