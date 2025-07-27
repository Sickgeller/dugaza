package kr.spring.house.service;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import kr.spring.api.client.HouseApiClient;
import kr.spring.api.dto.HouseDetailApiDto;
import kr.spring.api.mapper.HouseDetailApiMapper;
import kr.spring.api.service.CommonDataSyncSupportService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.spring.auth.security.CustomUserDetails;
import kr.spring.house.dao.HouseMapper;
import kr.spring.house.vo.HouseVO;
import kr.spring.seller.vo.HouseSellerDetailVO;
import kr.spring.util.CursorPagingUtil;
import kr.spring.wishlist.service.WishListService;
import kr.spring.wishlist.vo.WishListVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class HouseServiceImpl implements HouseService {

    private final HouseMapper houseMapper;
    private final WishListService wishListService;
    private final CommonDataSyncSupportService commonDataSyncSupportService;
    private final HouseApiClient houseApiClient;
    private final HouseDetailApiMapper houseDetailApiMapper;

    @Override //
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
    public List<HouseVO> selectListByCursor(CursorPagingUtil cursorPaging) {
        // CursorPagingUtil을 Map으로 변환
        Map<String, Object> map = cursorPaging.toMap();
        List<HouseVO> list = houseMapper.selectListByCursor(map);

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
    public boolean hasNextPage(CursorPagingUtil cursorPaging) {
        Map<String, Object> map = cursorPaging.toMap();
        return houseMapper.hasNextPage(map);
    }

    @Override
    public List<HouseVO> selectAdminList(Map<String, Object> map) {
        // 관리자용 목록 조회 - WishList 관련 로직 없이 단순히 mapper 호출
        return houseMapper.selectAdminList(map);
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
    public List<HouseVO> selectHousesWithSellerId(Long sellerId) {
        return houseMapper.selectHousesWithSellerId(sellerId);
    }

    @Override
    public void insertWithApi(Long contentId) {
        HouseDetailApiDto houseDetailApiDto = houseApiClient.getHouseDetailData(contentId);
        if (houseDetailApiDto != null) {
            commonDataSyncSupportService.insertOrUpdate(houseDetailApiMapper, houseDetailApiDto);
        }
	}

    @Override
    public List<HouseVO> selectListForApply(Map<String, Object> map) {
        return houseMapper.selectListForApply(map);
    }

	// 숙소 승인 관련 메서드들 구현
	@Override
	public void applyHouse(HouseSellerDetailVO houseSellerDetailVO) {
		houseSellerDetailVO.setStatus("suspending");
		houseMapper.insertHouseApplication(houseSellerDetailVO);
	}

	@Override
	public List<HouseSellerDetailVO> getPendingHouses() {
		log.info("getPendingHouses 서비스 메서드 호출됨");
		List<HouseSellerDetailVO> result = houseMapper.selectPendingHouses();
		log.info("getPendingHouses 결과: 개수={}", result.size());
		return result;
	}

	@Override
	public void approveHouse(Long houseId, Long sellerId) {
		Map<String, Object> params = Map.of(
			"houseId", houseId,
			"sellerId", sellerId,
			"status", "available"
		);
		houseMapper.updateHouseStatus(params);
	}

	@Override
	public void rejectHouse(Long houseId, Long sellerId) {
		houseMapper.updateHouseStatus(Map.of(
                "houseId", houseId,
                "sellerId", sellerId,
                "status", "deleted"
        ));
	}

	@Override
	public List<HouseSellerDetailVO> getHousesBySellerAndStatus(Long sellerId, String status) {
		Map<String, Object> params = new HashMap<>();
        params.put("sellerId", sellerId);
        if (status != null && !status.isEmpty()) {
            params.put("status", status);
        }
		return houseMapper.selectHousesBySellerAndStatus(params);
	}
    
    @Override
    public List<HouseSellerDetailVO> getHouseApplications(Map<String, Object> params) {
        return houseMapper.selectHouseApplications(params);
    }
    
    @Override
    public int getHouseApplicationCount(Map<String, Object> params) {
        return houseMapper.countHouseApplications(params);
    }
    
    @Override
    public boolean cancelHouseApplication(Map<String, Object> params) {
        return houseMapper.deleteHouseApplication(params) > 0;
    }
}
