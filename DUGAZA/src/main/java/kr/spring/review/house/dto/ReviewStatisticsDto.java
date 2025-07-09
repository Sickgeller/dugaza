package kr.spring.review.house.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.Map;

@Data
@Builder
public class ReviewStatisticsDto {
    // 기본 통계
    private Double averageRating;
    private Long totalCount;
    private Long recentCount; // 최근 30일
    
    // 별점별 분포
    private Long fiveStarCount;
    private Long fourStarCount;
    private Long threeStarCount;
    private Long twoStarCount;
    private Long oneStarCount;
    
    // 별점별 비율
    private Double fiveStarRatio;
    private Double fourStarRatio;
    private Double threeStarRatio;
    private Double twoStarRatio;
    private Double oneStarRatio;
    
    // 월별 트렌드 (최근 6개월)
    private Map<String, Long> monthlyTrend;
    

    
    /**
     * 별점별 비율 계산
     */
    public void calculateRatios() {
        if (totalCount > 0) {
            this.fiveStarRatio = (double) fiveStarCount / totalCount * 100;
            this.fourStarRatio = (double) fourStarCount / totalCount * 100;
            this.threeStarRatio = (double) threeStarCount / totalCount * 100;
            this.twoStarRatio = (double) twoStarCount / totalCount * 100;
            this.oneStarRatio = (double) oneStarCount / totalCount * 100;
        }
    }
} 