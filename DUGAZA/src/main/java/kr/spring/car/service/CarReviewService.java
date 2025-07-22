package kr.spring.car.service;

import kr.spring.car.vo.CarReviewVO;
import java.util.List;

public interface CarReviewService {
    void registerReview(CarReviewVO reviewVO);
    void deleteReview(Long reviewId);
    CarReviewVO getReview(Long reviewId);
    List<CarReviewVO> getReviewsByCar(Long carId);
} 