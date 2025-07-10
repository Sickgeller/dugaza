package kr.spring.review.base.service;

import kr.spring.review.base.dao.BaseReviewMapper;
import kr.spring.review.base.vo.ReviewStatisticsVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewStatisticsServiceImpl implements ReviewStatisticsService {
    
    private final BaseReviewMapper baseReviewMapper;
    
    @Override
    public ReviewStatisticsVO getReviewStatistics() {
        log.info("전체 리뷰 통계 조회 (DB에서 직접 조회)");
        
        try {
            ReviewStatisticsVO statistics = baseReviewMapper.getReviewStatistics();
            
            // 최근 30일 리뷰 수 추가
            Long recentCount = baseReviewMapper.getRecentReviewCount();
            statistics.setRecentCount(recentCount);
            
            // 월별 트렌드 추가
            List<Map<String, Object>> monthlyTrend = baseReviewMapper.getMonthlyReviewTrend();
            Map<String, Long> trendMap = monthlyTrend.stream()
                .collect(Collectors.toMap(
                    map -> (String) map.get("month"),
                    map -> ((Number) map.get("count")).longValue()
                ));
            statistics.setMonthlyTrend(trendMap);
            
            return statistics;
        } catch (Exception e) {
            log.error("전체 리뷰 통계 조회 중 오류 발생: error={}", e.getMessage(), e);
            return ReviewStatisticsVO.builder().build();
        }
    }
    
    @Override
    public ReviewStatisticsVO getReviewStatisticsBySeller(Long sellerId) {
        log.info("판매자별 리뷰 통계 조회: sellerId = {}", sellerId);
        
        try {
            ReviewStatisticsVO statistics = baseReviewMapper.getReviewStatisticsBySeller(sellerId);
            return statistics != null ? statistics : ReviewStatisticsVO.builder().build();
        } catch (Exception e) {
            log.error("판매자별 리뷰 통계 조회 중 오류 발생: sellerId={}, error={}", sellerId, e.getMessage(), e);
            return ReviewStatisticsVO.builder().build();
        }
    }
    
    @Override
    public ReviewStatisticsVO getReviewStatisticsByHouse(Long houseId) {
        log.info("숙소별 리뷰 통계 조회: houseId = {}", houseId);
        
        try {
            ReviewStatisticsVO statistics = baseReviewMapper.getReviewStatisticsByHouse(houseId);
            return statistics != null ? statistics : ReviewStatisticsVO.builder().build();
        } catch (Exception e) {
            log.error("숙소별 리뷰 통계 조회 중 오류 발생: houseId={}, error={}", houseId, e.getMessage(), e);
            return ReviewStatisticsVO.builder().build();
        }
    }
} 