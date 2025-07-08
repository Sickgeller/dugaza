package kr.spring.review.house.service;

import kr.spring.review.house.vo.HouseReviewVO;
import java.util.List;

public interface HouseReviewService {
    public List<HouseReviewVO> getRecentlyReviews(Long sellerId);
} 