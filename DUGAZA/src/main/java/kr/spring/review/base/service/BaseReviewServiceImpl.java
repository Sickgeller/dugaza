package kr.spring.review.base.service;

import kr.spring.review.base.dao.BaseReviewMapper;
import kr.spring.review.base.vo.BaseReviewVO;
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
public class BaseReviewServiceImpl implements BaseReviewService {
    
    private final BaseReviewMapper baseReviewMapper;
    
    @Override
    public List<BaseReviewVO> getRecentlyReviews(Long sellerId) {
        try {
            // 최근 5개의 리뷰만 가져오기
            List<BaseReviewVO> result = baseReviewMapper.findHouseReviewBySellerId(sellerId, 1, 5);
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
    public List<BaseReviewVO> getReviews(Long sellerId, int page, int pageSize) {
        List<BaseReviewVO> result = baseReviewMapper.findHouseReviewBySellerId(sellerId, (page - 1) * pageSize, page * pageSize);
        if(result.isEmpty()){
            return new ArrayList<>();
        }
        return result;
    }

	@Override
	public List<BaseReviewVO> getHouseReviews(Long houseId, int page, int pageSize) {
		List<BaseReviewVO> result = baseReviewMapper.findHouseReviewByHouseId(houseId, (page - 1) * pageSize, page * pageSize);
        if(result.isEmpty()){
            return new ArrayList<>();
        }
        return result;
	}

	@Override
	public void writeReview(BaseReviewVO vo) {
		baseReviewMapper.writeReview(vo);
	}
	
	@Override
	public List<BaseReviewVO> getReviewsByMember(Long memberId) {
		try {
			List<BaseReviewVO> result = baseReviewMapper.findReviewsByMemberId(memberId);
			if(result.isEmpty()){
				return new ArrayList<>();
			}
			return result;
		} catch (Exception e) {
			log.error("회원별 리뷰 조회 중 오류 발생: memberId={}, error={}", memberId, e.getMessage(), e);
			return new ArrayList<>();
		}
	}
} 