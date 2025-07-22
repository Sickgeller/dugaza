package kr.spring.community.service;

import java.util.List;
import java.util.Map;

import kr.spring.community.vo.CommunityPostVO;

public interface CommunityService {

    // 게시글 등록
    void insertPost(CommunityPostVO post);

    // 게시글 목록 조회
    List<CommunityPostVO> selectPostList(Map<String, Object> map);

    // 게시글 수 조회
    int selectPostCount(Map<String, Object> map);

    // 게시글 상세 조회
    CommunityPostVO selectPost(Long id);

    // 게시글 수정
    void updatePost(CommunityPostVO post);

    // 게시글 삭제
    void deletePost(Long id);

    // 게시글 조회수 증가
    void incrementViewCount(Long id);

    // 좋아요 토글
    boolean toggleLike(Long postId, Long memberId);

    // 좋아요 여부 확인
    boolean isLiked(Long postId, Long memberId);

}
