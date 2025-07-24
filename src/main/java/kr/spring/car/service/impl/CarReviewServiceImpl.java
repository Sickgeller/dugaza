package kr.spring.car.service.impl;

import kr.spring.car.dao.CarReviewMapper;
import kr.spring.car.service.CarReviewService;
import kr.spring.car.vo.CarReviewVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CarReviewServiceImpl implements CarReviewService {
    private final CarReviewMapper carReviewMapper;

    @Override
    public void registerReview(CarReviewVO reviewVO) {
        carReviewMapper.insertReview(reviewVO);
    }

    @Override
    public void deleteReview(Long reviewId) {
        carReviewMapper.deleteReview(reviewId);
    }

    @Override
    public CarReviewVO getReview(Long reviewId) {
        return carReviewMapper.selectReview(reviewId);
    }

    @Override
    public List<CarReviewVO> getReviewsByCar(Long carId) {
        return carReviewMapper.selectReviewsByCar(carId);
    }
} 