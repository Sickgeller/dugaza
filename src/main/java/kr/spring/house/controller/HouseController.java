package kr.spring.house.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.spring.tour.service.TourService;
import kr.spring.tour.vo.TourVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.ResponseEntity;

import jakarta.servlet.http.HttpSession;
import kr.spring.auth.security.CustomUserDetails;
import kr.spring.house.service.HouseService;
import kr.spring.house.vo.HouseVO;
import kr.spring.member.vo.MemberVO;
import kr.spring.review.base.vo.BaseReviewVO;
import kr.spring.review.base.vo.ReviewStatisticsVO;
import kr.spring.review.base.service.BaseReviewService;
import kr.spring.review.base.service.ReviewStatisticsService;
import kr.spring.room.service.RoomService;
import kr.spring.room.vo.RoomDetailVO;
import kr.spring.seller.service.SellerService;
import kr.spring.seller.vo.HouseSellerDetailVO;
import kr.spring.util.PagingUtil;
import kr.spring.wishlist.service.WishListService;
import kr.spring.wishlist.vo.WishListVO;
import lombok.extern.slf4j.Slf4j;
import kr.spring.util.CursorPagingUtil;

import java.util.function.Supplier;

@Slf4j
@Controller
@RequestMapping("/house")
@RequiredArgsConstructor
public class HouseController {

	private final HouseService houseService;
	private final BaseReviewService baseReviewService;
	private final ReviewStatisticsService reviewStatisticsService;
	private final WishListService wishListService;
	private final RoomService roomService;
//	private final SellerService sellerService;
	private final TourService tourService;

	// 공통 정보 조회 유틸
	public static <T> T getOrInsertAndGet(Supplier<T> select, Runnable insert) {
		T result = select.get();
		if (result == null) {
			insert.run();
			result = select.get();
		}
		return result;
	}

	@GetMapping("")
	public String accommodationMain(
			@RequestParam(name = "cursor", required = false) Long cursor,
			@RequestParam(name = "pageSize", defaultValue = "9") int pageSize,
			@RequestParam(name = "keyword", defaultValue = "") String keyword,
			@RequestParam(required = false, name = "cat3") String cat3,
			@RequestParam(name = "sort", defaultValue = "latest") String sort,
			@RequestParam(name = "checkIn", required = false) String checkIn,
            @RequestParam(name = "checkOut", required = false) String checkOut,
			Model model) {

		log.info("숙소 메인 페이지 요청: cursor={}, pageSize={}, keyword={}, cat3={}, sort={}", 
				cursor, pageSize, keyword, cat3, sort);

		// 커서 기반 페이지네이션 유틸 생성
		CursorPagingUtil cursorPaging = new CursorPagingUtil(cursor, pageSize, keyword, cat3, sort);
		
		// 데이터 조회
		List<HouseVO> list = houseService.selectListByCursor(cursorPaging);
		
		// 다음 페이지 존재 여부 확인
		boolean hasNext = houseService.hasNextPage(cursorPaging);
		cursorPaging.setHasNext(hasNext);
		
		// 다음 페이지 커서 계산 (마지막 항목의 ID)
		Long nextCursor = null;
		if (!list.isEmpty() && hasNext) {
			nextCursor = list.get(list.size() - 1).getContentId();
		}
		
		// 이전 페이지 커서 (현재 커서가 있으면 그대로 사용)
		Long prevCursor = cursor;
		
		// 페이지네이션 HTML 생성
		String paginationHtml = cursorPaging.generatePaginationHtml("/house", nextCursor, prevCursor);

		model.addAttribute("list", list);
		model.addAttribute("keyword", keyword);
		model.addAttribute("count", list.size());
		model.addAttribute("pageSize", pageSize);
		model.addAttribute("hasNext", hasNext);
		model.addAttribute("nextCursor", nextCursor);
		model.addAttribute("prevCursor", prevCursor);
		model.addAttribute("pagination", paginationHtml);
		model.addAttribute("cursorPaging", cursorPaging);
		model.addAttribute("cat3", cat3); // 뷰에서 활성화된 버튼 표시를 위해 전달
		model.addAttribute("sort", sort);
		model.addAttribute("checkIn", checkIn);
		model.addAttribute("checkOut", checkOut);
		
		return "views/sample/accommodation";
	}

	@GetMapping("/list")
	public String accommodationList(
			@RequestParam(name = "cursor", required = false) Long cursor,
			@RequestParam(name = "pageSize", defaultValue = "9") int pageSize,
			@RequestParam(name = "keyword", defaultValue = "") String keyword,
			@RequestParam(required = false, name = "cat3") String cat3,
			@RequestParam(name = "sort", defaultValue = "latest") String sort,
			@RequestParam(name = "checkIn", required = false) String checkIn,
            @RequestParam(name = "checkOut", required = false) String checkOut,
			Model model) {

		// 커서 기반 페이지네이션 유틸 생성
		CursorPagingUtil cursorPaging = new CursorPagingUtil(cursor, pageSize, keyword, cat3, sort);
		
		// 데이터 조회
		List<HouseVO> list = houseService.selectListByCursor(cursorPaging);
		
		// 다음 페이지 존재 여부 확인
		boolean hasNext = houseService.hasNextPage(cursorPaging);
		cursorPaging.setHasNext(hasNext);
		
		// 다음 페이지 커서 계산 (마지막 항목의 ID)
		Long nextCursor = null;
		if (!list.isEmpty() && hasNext) {
			nextCursor = list.get(list.size() - 1).getContentId();
		}
		
		// 이전 페이지 커서 (현재 커서가 있으면 그대로 사용)
		Long prevCursor = cursor;
		
		// 페이지네이션 HTML 생성
		String paginationHtml = cursorPaging.generatePaginationHtml("/house", nextCursor, prevCursor);

		model.addAttribute("list", list);
		model.addAttribute("count", list.size());
		model.addAttribute("pageSize", pageSize);
		model.addAttribute("hasNext", hasNext);
		model.addAttribute("nextCursor", nextCursor);
		model.addAttribute("prevCursor", prevCursor);
		model.addAttribute("pagination", paginationHtml);
		model.addAttribute("cursorPaging", cursorPaging);
		model.addAttribute("cat3", cat3); 
		model.addAttribute("sort", sort);
		model.addAttribute("checkIn", checkIn);
		model.addAttribute("checkOut", checkOut);

		return "views/sample/accommodation :: #accommodation-list";
	}

	// 항목 자세히 보기
	@GetMapping("/detail")
	public String houseDetail(@RequestParam(name = "contentId") Long contentId,
                         Model model, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
    // 숙소 정보
    HouseVO vo = getOrInsertAndGet(() -> houseService.selectHouse(contentId), () -> houseService.insertWithApi(contentId));
    if (vo == null) {
        model.addAttribute("message", "정보를 가져올 수 없는 숙소입니다.");
        model.addAttribute("redirectUrl", "/house");
        return "views/common/message";
    }
    // 숙소에 찜 여부
    WishListVO wish_vo = new WishListVO();
    wish_vo.setContentId(contentId);
    wish_vo.setContentType(32L); // 숙소 contentTypeId
    Long memberId = -1L;
    WishListVO db_wish = null;
    if (customUserDetails != null && customUserDetails.getMember() != null) {
        memberId = customUserDetails.getMember().getMemberId();
        wish_vo.setMemberId(memberId);
        db_wish = wishListService.selectWishList(wish_vo);
    }
    ReviewStatisticsVO status = reviewStatisticsService.getReviewStatisticsByHouse(contentId);
    model.addAttribute("info", vo);
    model.addAttribute("wish", db_wish);
    model.addAttribute("status", status);
    return "views/house/house-detail";
}

	// 리뷰 작성
	@PostMapping("/saveReview")
	public String saveReview(@ModelAttribute BaseReviewVO reviewDTO, @AuthenticationPrincipal CustomUserDetails userDetails) {
		MemberVO member = userDetails.getMember();

		reviewDTO.setMemberId(member.getMemberId());
		reviewDTO.setStatus(1);
		reviewDTO.setContentTypeId(32L); // 숙소 타입 ID

		baseReviewService.writeReview(reviewDTO);
		log.debug("<<리뷰 작성>> 사용자 id : {}", member.getMemberId());

		return "redirect:/house/detail?contentId=" + reviewDTO.getContentId();
	}

	
}

