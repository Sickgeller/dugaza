package kr.spring.review.house.service;

import kr.spring.review.house.dao.HouseReviewMapper;
import kr.spring.review.house.vo.HouseReviewVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class HouseReviewServiceImpl implements HouseReviewService {
    
    private final HouseReviewMapper houseReviewMapper;
    
    @Override
    public List<HouseReviewVO> getRecentlyReviews(Long sellerId) {
        // 최근 5개의 리뷰만 가져오기
        return houseReviewMapper.findHouseReviewBySellerId(sellerId, 1, 5);
    }
} 