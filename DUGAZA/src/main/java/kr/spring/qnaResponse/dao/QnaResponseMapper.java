package kr.spring.qnaResponse.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.spring.qnaQuestion.vo.QnaQuestionVO;
import kr.spring.qnaResponse.vo.QnaResponseVO;

@Mapper
public interface QnaResponseMapper {
    void insertResponse(QnaResponseVO responseVO);
    void updateIsAnswered(@Param("qna_id") Long qna_id, @Param("is_answered") String is_answered);
    QnaResponseVO getAnswerByQnaId(@Param("qna_id") Long qna_id);
    QnaQuestionVO getQnaById(Long qnaId);
    List<QnaQuestionVO> getAllQnaList(); // 관리자용 전체 조회
    void updateResponse(QnaResponseVO responseVO);
}