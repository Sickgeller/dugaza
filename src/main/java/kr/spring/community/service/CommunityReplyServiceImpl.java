package kr.spring.community.service;

import java.util.List;

import org.springframework.stereotype.Service;

import kr.spring.community.dao.CommunityMapper;
import kr.spring.community.dao.CommunityReplyMapper;
import kr.spring.community.vo.CommunityReplyVO;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommunityReplyServiceImpl implements CommunityReplyService {

    private final CommunityReplyMapper replyMapper;
    private final CommunityMapper postMapper;

    @Override
    public List<CommunityReplyVO> selectReplyList(Long postId) {
        return replyMapper.selectReplyList(postId);
    }

    @Override
    public CommunityReplyVO getReply(Long replyId) {
        return replyMapper.getReply(replyId);
    }

    @Override
    public void insertReply(CommunityReplyVO reply) {
        replyMapper.insertReply(reply);
        postMapper.updateCommentCount(reply.getPostId(), 1); // 댓글 수 +1
    }

    @Override
    public void deleteReply(Long replyId) {
        CommunityReplyVO reply = replyMapper.getReply(replyId);
        Long postId = reply.getPostId();

        replyMapper.deleteReply(replyId);
        postMapper.updateCommentCount(postId, -1);
    }


    @Override
    public void updateReply(CommunityReplyVO reply) {
        reply.setIsModified(1); // 수정됨 표시
        replyMapper.updateReply(reply);
    }

}
