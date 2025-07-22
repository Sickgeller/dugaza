package kr.spring.community.dao;

import java.util.List;

import kr.spring.community.vo.CommunityReplyVO;

public interface CommunityReplyMapper {
    List<CommunityReplyVO> selectReplyList(Long postId);
    CommunityReplyVO getReply(Long replyId);
    void insertReply(CommunityReplyVO reply);
    void deleteReply(Long replyId);
    void updateReply(CommunityReplyVO reply);
}
