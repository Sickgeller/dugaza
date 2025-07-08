package kr.spring.review.house.service;

import kr.spring.review.house.dto.ReviewStatisticsDto;

public interface ReviewStatisticsService {
    
    /**
     * 전체 리뷰 통계 조회
     */
    ReviewStatisticsDto getReviewStatistics();
    
    /**
     * 판매자별 리뷰 통계 조회
     */
    ReviewStatisticsDto getReviewStatisticsBySeller(Long sellerId);
    
    /**
     * 숙소별 리뷰 통계 조회
     */
    ReviewStatisticsDto getReviewStatisticsByHouse(Long houseId);
} 