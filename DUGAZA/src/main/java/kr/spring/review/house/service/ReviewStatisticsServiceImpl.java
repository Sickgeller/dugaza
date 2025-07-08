package kr.spring.review.house.service;

import kr.spring.review.house.dao.HouseReviewMapper;
import kr.spring.review.house.dto.ReviewStatisticsDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewStatisticsServiceImpl implements ReviewStatisticsService {
    
    private final HouseReviewMapper houseReviewMapper;
    
    @Override
    public ReviewStatisticsDto getReviewStatistics() {
        log.info("전체 리뷰 통계 조회 (DB에서 직접 조회)");
        
        ReviewStatisticsDto statistics = houseReviewMapper.getReviewStatistics();
        
        // 최근 30일 리뷰 수 추가
        Long recentCount = houseReviewMapper.getRecentReviewCount();
        statistics.setRecentCount(recentCount);
        
        // 월별 트렌드 추가
        List<Map<String, Object>> monthlyTrend = houseReviewMapper.getMonthlyReviewTrend();
        Map<String, Long> trendMap = monthlyTrend.stream()
            .collect(Collectors.toMap(
                map -> (String) map.get("month"),
                map -> ((Number) map.get("count")).longValue()
            ));
        statistics.setMonthlyTrend(trendMap);
        
        // 비율 계산
        statistics.calculateRatios();
        
        return statistics;
    }
    
    @Override
    public ReviewStatisticsDto getReviewStatisticsBySeller(Long sellerId) {
        log.info("판매자별 리뷰 통계 조회: sellerId = {}", sellerId);
        
        ReviewStatisticsDto statistics = houseReviewMapper.getReviewStatisticsBySeller(sellerId);
        
        if (statistics != null) {
            statistics.calculateRatios();
        }
        
        return statistics;
    }
    
    @Override
    public ReviewStatisticsDto getReviewStatisticsByHouse(Long houseId) {
        log.info("숙소별 리뷰 통계 조회: houseId = {}", houseId);
        
        ReviewStatisticsDto statistics = houseReviewMapper.getReviewStatisticsByHouse(houseId);
        
        if (statistics != null) {
            statistics.calculateRatios();
        }
        
        return statistics;
    }
} 