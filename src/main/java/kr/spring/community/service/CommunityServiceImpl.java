package kr.spring.community.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import kr.spring.community.dao.CommunityMapper;
import kr.spring.community.vo.CommunityPostVO;
import kr.spring.util.CursorPagingUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommunityServiceImpl implements CommunityService {

    private final CommunityMapper communityMapper;

    @Override
    public void insertPost(CommunityPostVO post) {
        communityMapper.insertPost(post);
    }

    @Override
    public List<CommunityPostVO> selectPostList(Map<String, Object> map) {
        return communityMapper.selectPostList(map);
    }
    
    @Override
    public List<CommunityPostVO> selectPostListByCursor(CursorPagingUtil cursorPaging) {
        // CursorPagingUtil을 Map으로 변환
        Map<String, Object> map = cursorPaging.toMap();
        return communityMapper.selectPostListByCursor(map);
    }
    
    @Override
    public boolean hasNextPage(CursorPagingUtil cursorPaging) {
        Map<String, Object> map = cursorPaging.toMap();
        return communityMapper.hasNextPage(map);
    }

    @Override
    public int selectPostCount(Map<String, Object> map) {
        return communityMapper.selectPostCount(map);
    }

    @Override
    public CommunityPostVO selectPost(Long id) {
        return communityMapper.selectPost(id);
    }

    @Override
    public void updatePost(CommunityPostVO post) {
        communityMapper.updatePost(post);
    }

    @Override
    public void deletePost(Long id) {
        communityMapper.deletePost(id);
    }

    @Override
    public void incrementViewCount(Long id) {
        communityMapper.incrementViewCount(id);
    }

    @Override
    public boolean toggleLike(Long postId, Long memberId) {
        boolean liked = communityMapper.isLiked(postId, memberId) > 0;

        if (liked) {
            communityMapper.deleteLike(postId, memberId);
            communityMapper.decrementLikeCount(postId);
            return false; // 취소
        } else {
            communityMapper.insertLike(postId, memberId);
            communityMapper.incrementLikeCount(postId);
            return true; // 좋아요
        }
    }

    @Override
    public boolean isLiked(Long postId, Long memberId) {
        return communityMapper.isLiked(postId, memberId) > 0;
    }

}
