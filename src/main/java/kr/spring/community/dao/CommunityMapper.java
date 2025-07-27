package kr.spring.community.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.spring.community.vo.CommunityPostVO;

@Mapper
public interface CommunityMapper {
    // 글 등록
    void insertPost(CommunityPostVO post);

    // 글 목록
    List<CommunityPostVO> selectPostList(Map<String, Object> map);
    
    // 커서 기반 페이지네이션을 위한 메서드들
    List<CommunityPostVO> selectPostListByCursor(Map<String, Object> map);
    boolean hasNextPage(Map<String, Object> map);

    // 글 갯수 (페이징용)
    int selectPostCount(Map<String, Object> map);

    // 글 상세
    CommunityPostVO selectPost(Long id);

    // 글 수정
    void updatePost(CommunityPostVO post);

    // 글 삭제
    void deletePost(Long id);
    
    //댓글 카운트
    void updateCommentCount(Long postId, int amount);
    
    //조회수 증가
    void incrementViewCount(Long id);
    
    //좋아요 증가
    void incrementLikeCount(Long id);
    
    //좋아요 여부 확인
    int isLiked(@Param("postId") Long postId, @Param("memberId") Long memberId);
    
    //좋아요 삭제
    void deleteLike(@Param("postId") Long postId, @Param("memberId") Long memberId);
    
    //좋아요 등록
    void insertLike(@Param("postId") Long postId, @Param("memberId") Long memberId);
    
    //좋아요 감소
    void decrementLikeCount(Long postId);
}
