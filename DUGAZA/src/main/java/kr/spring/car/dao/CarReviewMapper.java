package kr.spring.car.dao;

import kr.spring.car.vo.CarReviewVO;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface CarReviewMapper {
    void insertReview(CarReviewVO reviewVO);
    void deleteReview(Long reviewId);
    CarReviewVO selectReview(Long reviewId);
    List<CarReviewVO> selectReviewsByCar(Long carId);
} 