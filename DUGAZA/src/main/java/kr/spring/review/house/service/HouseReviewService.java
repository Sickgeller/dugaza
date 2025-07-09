package kr.spring.review.house.service;

import kr.spring.review.house.vo.HouseReviewVO;
import java.util.List;

public interface HouseReviewService {
    public List<HouseReviewVO> getRecentlyReviews(Long sellerId);
    public List<HouseReviewVO> getReviews(Long sellerId, int page, int pageSize);
    public List<HouseReviewVO> getHouseReviews(Long houseId, int page, int pageSize);
    public void writeReview(HouseReviewVO vo);
}