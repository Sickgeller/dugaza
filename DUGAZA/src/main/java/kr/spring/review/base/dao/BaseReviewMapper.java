package kr.spring.review.base.dao;

import kr.spring.review.base.vo.ReviewStatisticsVO;
import kr.spring.review.base.vo.BaseReviewVO;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface BaseReviewMapper {
    public List<BaseReviewVO> findHouseReviewBySellerId(@Param("sellerId") Long sellerId,
                                                        @Param("startRow") int startRow,
                                                        @Param("endRow") int endRow);
    
    public List<BaseReviewVO> findHouseReviewByHouseId(@Param("houseId") Long houseId,
                                                       @Param("startRow") int startRow,
                                                       @Param("endRow") int endRow);
    
    /**
     * 전체 리뷰 통계 조회
     */
    ReviewStatisticsVO getReviewStatistics();
    
    /**
     * 판매자별 리뷰 통계 조회
     */
    ReviewStatisticsVO getReviewStatisticsBySeller(@Param("sellerId") Long sellerId);
    
    /**
     * 숙소별 리뷰 통계 조회
     */
    ReviewStatisticsVO getReviewStatisticsByHouse(@Param("houseId") Long houseId);
    
    /**
     * 월별 리뷰 트렌드 조회 (최근 6개월)
     */
    List<Map<String, Object>> getMonthlyReviewTrend();
    
    /**
     * 최근 30일 리뷰 수 조회
     */
    Long getRecentReviewCount();
    
    // 리뷰 작성
    public void writeReview(BaseReviewVO vo);
}
