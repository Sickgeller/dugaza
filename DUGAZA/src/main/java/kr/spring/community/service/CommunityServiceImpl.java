package kr.spring.community.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import kr.spring.community.dao.CommunityMapper;
import kr.spring.community.vo.CommunityPostVO;
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
}
