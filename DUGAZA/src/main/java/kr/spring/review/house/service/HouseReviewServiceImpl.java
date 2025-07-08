package kr.spring.review.house.service;

import kr.spring.review.house.dao.HouseReviewMapper;
import kr.spring.review.house.vo.HouseReviewVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class HouseReviewServiceImpl implements HouseReviewService {
    
    private final HouseReviewMapper houseReviewMapper;
    
    @Override
    public List<HouseReviewVO> getRecentlyReviews(Long sellerId) {
        try {
            // 최근 5개의 리뷰만 가져오기
            List<HouseReviewVO> result = houseReviewMapper.findHouseReviewBySellerId(sellerId, 1, 5);
            if(result.isEmpty()){
                return new ArrayList<>();
            }
            return result;
        } catch (Exception e) {
            log.error("리뷰 조회 중 오류 발생: sellerId={}, error={}", sellerId, e.getMessage(), e);
            return new ArrayList<>();
        }
    }
    @Override
    public List<HouseReviewVO> getReviews(Long sellerId, int page, int pageSize) {
        List<HouseReviewVO> result = houseReviewMapper.findHouseReviewBySellerId(sellerId, (page - 1) * pageSize, page * pageSize);
        if(result.isEmpty()){
            return new ArrayList<>();
        }
        return result;
    }
} 