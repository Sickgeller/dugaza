package kr.spring.review.base.controller;

import kr.spring.review.base.vo.ReviewStatisticsVO;
import kr.spring.review.base.service.ReviewStatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/review/statistics")
@RequiredArgsConstructor
public class ReviewStatisticsController {
    
    private final ReviewStatisticsService reviewStatisticsService;
    
    /**
     * 전체 리뷰 통계 조회
     */
    @GetMapping("/all")
    public ResponseEntity<ReviewStatisticsVO> getAllReviewStatistics() {
        log.info("전체 리뷰 통계 API 호출");
        
        ReviewStatisticsVO statistics = reviewStatisticsService.getReviewStatistics();
        
        return ResponseEntity.ok(statistics);
    }
    
    /**
     * 판매자별 리뷰 통계 조회
     */
    @GetMapping("/seller/{sellerId}")
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<ReviewStatisticsVO> getSellerReviewStatistics(@PathVariable Long sellerId) {
        log.info("판매자별 리뷰 통계 API 호출: sellerId = {}", sellerId);
        
        ReviewStatisticsVO statistics = reviewStatisticsService.getReviewStatisticsBySeller(sellerId);
        if (statistics == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(statistics);
    }
    
    /**
     * 숙소별 리뷰 통계 조회
     */
    @GetMapping("/house/{houseId}")
    public ResponseEntity<ReviewStatisticsVO> getHouseReviewStatistics(@PathVariable Long houseId) {
        log.info("숙소별 리뷰 통계 API 호출: houseId = {}", houseId);
        
        ReviewStatisticsVO statistics = reviewStatisticsService.getReviewStatisticsByHouse(houseId);
        
        if (statistics == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(statistics);
    }
} 