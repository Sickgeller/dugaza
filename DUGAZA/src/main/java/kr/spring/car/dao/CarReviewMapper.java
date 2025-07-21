package kr.spring.car.dao;

import kr.spring.car.vo.CarReviewVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface CarReviewMapper {
    void insertReview(CarReviewVO reviewVO);
    void deleteReview(Long reviewId);
    CarReviewVO selectReview(Long reviewId);
    List<CarReviewVO> selectReviewsByCar(Long carId);
    
    /**
     * 모든 차량 리뷰 조회 (관리자용)
     */
    List<CarReviewVO> selectAllCarReviews();
    
    /**
     * 차량 리뷰 상태 업데이트
     */
    void updateReviewStatus(@Param("reviewId") Long reviewId, @Param("status") Integer status);
} 