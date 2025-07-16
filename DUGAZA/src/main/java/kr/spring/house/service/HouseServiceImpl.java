package kr.spring.house.service;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.spring.auth.security.CustomUserDetails;
import kr.spring.house.dao.HouseMapper;
import kr.spring.house.vo.HouseVO;
import kr.spring.wishlist.service.WishListService;
import kr.spring.wishlist.vo.WishListVO;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class HouseServiceImpl implements HouseService {

    private final HouseMapper houseMapper;
    private final WishListService wishListService;

    @Override
    public List<HouseVO> selectList(Map<String, Object> map) {
        List<HouseVO> list = houseMapper.selectList(map);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            long memberId = userDetails.getMember().getMemberId();

            for (HouseVO house : list) {
                try {
                    WishListVO wishListVO = new WishListVO();
                    wishListVO.setMemberId(memberId);
                    wishListVO.setContentId(house.getContentId());
                    wishListVO.setContentType(house.getContentTypeId().longValue());

                    if (wishListService.selectWishList(wishListVO) != null) {
                        house.setWished(true);
                    } else {
                        house.setWished(false);
                    }
                } catch (NumberFormatException e) {
                    house.setWished(false);
                }
            }
        } else {
            for (HouseVO house : list) {
                house.setWished(false);
            }
        }

        return list;
    }

    @Override
    public Integer selectRowCount(Map<String, Object> map) {
        return houseMapper.selectRowCount(map);
    }

    @Override
    public HouseVO selectHouse(Long id) {
        return houseMapper.selectHouse(id);
    }

	@Override
	public HouseVO selectHouseWithSellerId(Long sellerId) {
		return houseMapper.selectHouseWithSellerId(sellerId);
	}

	@Override
	public void insertWithApi(Long contentId) {
		// TODO Auto-generated method stub
		
	}
}
