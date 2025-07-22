package kr.spring.tour.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.spring.auth.security.CustomUserDetails;
import kr.spring.tour.dao.TourMapper;
import kr.spring.tour.vo.TourVO;
import kr.spring.wishlist.service.WishListService;
import kr.spring.wishlist.vo.WishListVO;

@Service
@Transactional
public class TourServiceImpl implements TourService{
	
	@Autowired
	private TourMapper tourMapper;
	
	@Autowired
	private WishListService wishListService;
	
	@Override
	public Integer selectRowCount() {
		return tourMapper.selectRowCount();
	}

	@Override
	public List<TourVO> selectList(Map<String, Object> map) {
		List<TourVO> list = tourMapper.selectList(map);
		
		// 로그인한 사용자의 찜 상태 확인
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof CustomUserDetails) {
			CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
			long memberId = userDetails.getMember().getMemberId();

			for (TourVO tour : list) {
				try {
					WishListVO wishListVO = new WishListVO();
					wishListVO.setMemberId(memberId);
					wishListVO.setContentId(tour.getContentId());
					wishListVO.setContentType(tour.getContentTypeId().longValue());

					if (wishListService.selectWishList(wishListVO) != null) {
						tour.setWished(true);
					} else {
						tour.setWished(false);
					}
				} catch (NumberFormatException e) {
					tour.setWished(false);
				}
			}
		} else {
			for (TourVO tour : list) {
				tour.setWished(false);
			}
		}

		return list;
	}

	@Override
	public TourVO selectTourContent(Long contentId) {
		return tourMapper.selectTourContent(contentId);
	}
}
