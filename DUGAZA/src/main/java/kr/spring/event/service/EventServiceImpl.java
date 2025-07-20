package kr.spring.event.service;

import java.util.List;
import java.util.Map;

import kr.spring.api.client.EventApiClient;
import kr.spring.api.dto.EventDetailApiDto;
import kr.spring.api.mapper.EventDetailApiMapper;
import kr.spring.api.service.CommonDataSyncSupportService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.spring.auth.security.CustomUserDetails;
import kr.spring.event.dao.EventMapper;
import kr.spring.event.vo.EventVO;
import kr.spring.tour.vo.TourVO;
import kr.spring.wishlist.service.WishListService;
import kr.spring.wishlist.vo.WishListVO;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class EventServiceImpl implements EventService{
	
	private final EventMapper eventMapper;
	private final EventApiClient eventApiClient;
	private final CommonDataSyncSupportService commonDataSyncSupportService;
	private final EventDetailApiMapper eventDetailApiMapper;
	private final WishListService wishListService;

	@Override
	public Integer selectRowCount() {
		return eventMapper.selectRowCount();
	}

	@Override
	public List<TourVO> selectList(Map<String, Object> map) {
		List<TourVO> list = eventMapper.selectList(map);
		
		// 로그인한 사용자의 찜 상태 확인
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof CustomUserDetails) {
			CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
			long memberId = userDetails.getMember().getMemberId();

			for (TourVO event : list) {
				try {
					WishListVO wishListVO = new WishListVO();
					wishListVO.setMemberId(memberId);
					wishListVO.setContentId(event.getContentId());
					wishListVO.setContentType(event.getContentTypeId().longValue());

					if (wishListService.selectWishList(wishListVO) != null) {
						event.setWished(true);
					} else {
						event.setWished(false);
					}
				} catch (NumberFormatException e) {
					event.setWished(false);
				}
			}
		} else {
			for (TourVO event : list) {
				event.setWished(false);
			}
		}

		return list;
	}

	@Override
	public EventVO selectEvent(Long id) {
		return eventMapper.selectEvent(id);
	}

	@Override
	public EventVO selectEventWithApi(Long contentId) {
		EventDetailApiDto eventDetailApiDto = eventApiClient.getEventDetailData(contentId);
		if(eventDetailApiDto.getContentId() != null) {
			commonDataSyncSupportService.insertOrUpdate(eventDetailApiMapper, eventDetailApiDto);
        	return eventMapper.selectEvent(contentId);
		}else {
			return null;
		}
	}

	@Override
	public TourVO selectTourContent(Long contentId) {
		return eventMapper.selectTourContent(contentId);
	}
}
