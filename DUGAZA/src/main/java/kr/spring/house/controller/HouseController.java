package kr.spring.house.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import kr.spring.auth.security.CustomUserDetails;
import kr.spring.house.service.HouseService;
import kr.spring.house.vo.HouseVO;
import kr.spring.member.vo.MemberVO;
import kr.spring.review.base.vo.BaseReviewVO;
import kr.spring.review.base.vo.ReviewStatisticsVO;
import kr.spring.review.base.service.BaseReviewService;
import kr.spring.review.base.service.ReviewStatisticsService;
import kr.spring.util.PagingUtil;
import kr.spring.wishlist.service.WishListService;
import kr.spring.wishlist.vo.WishListVO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/house")
public class HouseController {

	@Autowired
	private HouseService houseService;
	
	@Autowired
	private BaseReviewService baseReviewService;
	
	@Autowired
	private ReviewStatisticsService reviewStatisticsService;
	
	@Autowired
	private WishListService wishListService;

	@GetMapping("")
	public String accommodationMain(@RequestParam(name = "pageNum", defaultValue="1") int pageNum,
			@RequestParam(name = "keyword", defaultValue = "") String keyword,
			@RequestParam(required = false, name = "cat3") String cat3,
			@RequestParam(name = "sort", defaultValue = "latest") String sort,
			Model model) {

		Map<String,Object> map = new HashMap<String,Object>();
		map.put("keyword", keyword);
		map.put("cat3", cat3);
		map.put("sort", sort);

		int count = houseService.selectRowCount(map);

		//페이지 처리
		PagingUtil page = new PagingUtil(null,keyword,
				pageNum,count,9,10,
				"");

		// 페이지네이션 링크에 cat3 파라미터를 유지하도록 설정
		String pageUrl = "/house"; 
		if (cat3 != null && !cat3.isEmpty()) {
			pageUrl += "?cat3=" + cat3;
		}
		// PagingUtil에 pageURL을 설정하는 메서드가 있다고 가정합니다.
		// 실제 PagingUtil 클래스의 메서드명에 따라 수정이 필요할 수 있습니다.
		// page.setPageURL(pageUrl);

		List<HouseVO> list = null;
		if(count > 0) {
			map.put("start", page.getStartRow());
			map.put("end", page.getEndRow());
			list = houseService.selectList(map);
		}
		model.addAttribute("count", count);
		model.addAttribute("list", list);
		model.addAttribute("page", page);
		model.addAttribute("cat3", cat3); // 뷰에서 활성화된 버튼 표시를 위해 전달
		model.addAttribute("sort", sort);
		return "views/sample/accommodation";
	}

	@GetMapping("/list")
	public String accommodationList(@RequestParam(name = "pageNum", defaultValue="1") int pageNum,
			@RequestParam(name = "keyword", defaultValue = "") String keyword,
			@RequestParam(required = false, name = "cat3") String cat3,
			@RequestParam(name = "sort", defaultValue = "latest") String sort,
			Model model) {

		Map<String,Object> map = new HashMap<String,Object>();
		map.put("keyword", keyword);
		map.put("cat3", cat3);
		map.put("sort", sort);

		int count = houseService.selectRowCount(map);

		//페이지 처리
		PagingUtil page = new PagingUtil(null,keyword,
				pageNum,count,9,10,
				"");

		List<HouseVO> list = null;
		if(count > 0) {
			map.put("start", page.getStartRow());
			map.put("end", page.getEndRow());
			list = houseService.selectList(map);
		}
		model.addAttribute("count", count);
		model.addAttribute("list", list);
		model.addAttribute("page", page);
		model.addAttribute("cat3", cat3); 
		model.addAttribute("sort", sort);

		return "views/sample/accommodation :: #accommodation-list";
	}

	// 항목 자세히 보기
	@GetMapping("/detail")
	public String houseDetail(@RequestParam(name = "contentId") Long contentId, Model model) {
		// 숙소 정보
		HouseVO vo = null;
			vo = houseService.selectHouse(contentId);
			if(vo == null) {
				houseService.insertWithApi(contentId);
				vo = houseService.selectHouse(contentId);
			}

		if (vo == null) {
			model.addAttribute("message", "정보를 가져올 수 없는 숙소입니다.");
			model.addAttribute("redirectUrl", "/house");
			return "views/common/message";
		}

		// 숙소별 리뷰 목록
		List<BaseReviewVO> reviewList = baseReviewService.getHouseReviews(contentId, 1, 10);
		// 숙소별 리뷰 통계
		ReviewStatisticsVO status = reviewStatisticsService.getReviewStatisticsByHouse(contentId);
		// 숙소에 찜 여부
		WishListVO wish_vo = new WishListVO();
		wish_vo.setContentId(contentId);
		MemberVO member = (MemberVO)model.getAttribute("member");
		wish_vo.setMemberId(member != null ? member.getMemberId() : -1);
		WishListVO db_wish = wishListService.selectWishList(wish_vo);
		

		model.addAttribute("info",vo);
		model.addAttribute("reviewList",reviewList);
		model.addAttribute("status", status);
		model.addAttribute("wish", db_wish);

		// API에서 정보를 제공하지 않을 때 공통 템플릿 사용
		if(vo == null) {
			// tour_content에서 기본 정보만 가져오기 (HouseService에 selectTourContent 메서드가 있다고 가정)
			// TourVO tourInfo = houseService.selectTourContent(contentId);
			// if(tourInfo != null) {
			//     model.addAttribute("info", tourInfo);
			//     model.addAttribute("contentTypeName", "숙소");
			//     model.addAttribute("reviewActionUrl", "/house/saveReview");
			//     return "views/common/content-detail-basic";
			// }
		}

		return "views/house/house-detail";
	}

	// 리뷰 작성
	@PostMapping("/saveReview")
	public String saveReview(@ModelAttribute BaseReviewVO reviewDTO, HttpSession session) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
		MemberVO member = userDetails.getMember();

		CustomUserDetails user = (CustomUserDetails)session.getAttribute("user");
		reviewDTO.setMemberId(user.getUserId());
		reviewDTO.setStatus(1);
		reviewDTO.setContentTypeId(32L); // 숙소 타입 ID

		baseReviewService.writeReview(reviewDTO);
		log.debug("<<리뷰 작성>> 사용자 id : {}", user.getUserId());

		return "redirect:/house/detail?contentId=" + reviewDTO.getContentId();
	}

}
