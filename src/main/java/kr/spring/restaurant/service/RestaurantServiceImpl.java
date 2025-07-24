package kr.spring.restaurant.service;

import java.util.List;
import java.util.Map;

import kr.spring.api.client.RestaurantDetailAplClient;
import kr.spring.api.dto.RestaurantDetailApiDto;
import kr.spring.api.mapper.RestaurantDetailApiMapper;
import kr.spring.api.service.CommonDataSyncSupportService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.spring.auth.security.CustomUserDetails;
import kr.spring.restaurant.dao.RestaurantMapper;
import kr.spring.restaurant.vo.RestaurantVO;
import kr.spring.tour.vo.TourVO;
import kr.spring.wishlist.service.WishListService;
import kr.spring.wishlist.vo.WishListVO;

@Service
@Transactional
@RequiredArgsConstructor
public class RestaurantServiceImpl implements RestaurantService{

	private final RestaurantMapper restaurantMapper;
	private final RestaurantDetailAplClient restaurantDetailAplClient;
	private final RestaurantDetailApiMapper restaurantDetailApiMapper;
	private final CommonDataSyncSupportService commonDataSyncSupportService;
	private final WishListService wishListService;

	@Override
	public Integer selectRowCount() {
		return restaurantMapper.selectRowCount();
	}

	@Override
	public List<TourVO> selectList(Map<String, Object> map) {
		List<TourVO> list = restaurantMapper.selectList(map);
		
		// 로그인한 사용자의 찜 상태 확인
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof CustomUserDetails) {
			CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
			long memberId = userDetails.getMember().getMemberId();

			for (TourVO restaurant : list) {
				try {
					WishListVO wishListVO = new WishListVO();
					wishListVO.setMemberId(memberId);
					wishListVO.setContentId(restaurant.getContentId());
					wishListVO.setContentType(restaurant.getContentTypeId().longValue());

					if (wishListService.selectWishList(wishListVO) != null) {
						restaurant.setWished(true);
					} else {
						restaurant.setWished(false);
					}
				} catch (NumberFormatException e) {
					restaurant.setWished(false);
				}
			}
		} else {
			for (TourVO restaurant : list) {
				restaurant.setWished(false);
			}
		}

		return list;
	}

	@Override
	public RestaurantVO selectRestaurant(Long id) {
		return restaurantMapper.selectRestaurant(id);
	}

	@Override
	public RestaurantVO selectRestaurantWithApi(Long contentId) {
		RestaurantVO result;
		RestaurantDetailApiDto dto = null;
		dto = restaurantDetailAplClient.getRestaurantDetailData(contentId);
		commonDataSyncSupportService.insertOrUpdate(restaurantDetailApiMapper,dto);
		result = selectRestaurant(contentId);
		return result;
	}
}
