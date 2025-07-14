package kr.spring.community.service;

import java.util.List;

import kr.spring.community.vo.CommunityReplyVO;

public interface CommunityReplyService {

    List<CommunityReplyVO> selectReplyList(Long postId);      // 댓글 목록
    CommunityReplyVO getReply(Long replyId);                  // 댓글 1개 조회
    void insertReply(CommunityReplyVO reply);                 // 댓글 작성
    void deleteReply(Long replyId);										// 댓글 삭제 (postId 필요: COMMENT_COUNT 갱신)
    void updateReply(CommunityReplyVO reply);                 // 댓글 수정
    
}
