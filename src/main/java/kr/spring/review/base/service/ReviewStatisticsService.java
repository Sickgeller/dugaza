package kr.spring.review.base.service;

import kr.spring.review.base.vo.ReviewStatisticsVO;

public interface ReviewStatisticsService {
    
    /**
     * 전체 리뷰 통계 조회
     */
    ReviewStatisticsVO getReviewStatistics();
    
    /**
     * 판매자별 리뷰 통계 조회
     */
    ReviewStatisticsVO getReviewStatisticsBySeller(Long sellerId);
    
    /**
     * 숙소별 리뷰 통계 조회
     */
    ReviewStatisticsVO getReviewStatisticsByHouse(Long houseId);
} 