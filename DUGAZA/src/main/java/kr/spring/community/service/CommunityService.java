package kr.spring.community.service;

import java.util.List;
import java.util.Map;

import kr.spring.community.vo.CommunityPostVO;

public interface CommunityService {
    void insertPost(CommunityPostVO post);
    List<CommunityPostVO> selectPostList(Map<String, Object> map);
    int selectPostCount(Map<String, Object> map);
    CommunityPostVO selectPost(Long id);
    void updatePost(CommunityPostVO post);
    void deletePost(Long id);
}
