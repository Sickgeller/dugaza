package kr.spring.review.base.service;

import kr.spring.review.base.vo.BaseReviewVO;

import java.util.List;

public interface BaseReviewService {
    public List<BaseReviewVO> getRecentlyReviews(Long sellerId);
    public List<BaseReviewVO> getReviews(Long sellerId, int page, int pageSize);
    public List<BaseReviewVO> getHouseReviews(Long houseId, int page, int pageSize);
    public void writeReview(BaseReviewVO vo);
    
    /**
     * 회원별 리뷰 조회
     */
    public List<BaseReviewVO> getReviewsByMember(Long memberId);
    public List<BaseReviewVO> getReviewsByContentType(String contentType, Long contentId, int page, int pageSize);
}