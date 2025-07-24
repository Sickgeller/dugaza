package kr.spring.qnaResponse.service;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import kr.spring.qnaQuestion.vo.QnaQuestionVO;
import kr.spring.qnaResponse.vo.QnaResponseVO;

public interface QnaResponseService {
    void insertResponse(QnaResponseVO responseVO);
    List<QnaQuestionVO> getAllQnaList();
    QnaResponseVO getAnswerByQnaId(Long qna_id);
    QnaQuestionVO getQnaById(Long qnaId);
    void updateIsAnswered(Long qna_id, String is_answered);
    void updateResponse(QnaResponseVO responseVO);
}