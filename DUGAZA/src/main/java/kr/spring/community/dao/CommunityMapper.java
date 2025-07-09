package kr.spring.community.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import kr.spring.community.vo.CommunityPostVO;

@Mapper
public interface CommunityMapper {
    // 글 등록
    void insertPost(CommunityPostVO post);

    // 글 목록
    List<CommunityPostVO> selectPostList(Map<String, Object> map);

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
}
