package kr.spring.restaurant.controller;

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
import kr.spring.house.vo.HouseVO;
import kr.spring.member.vo.MemberVO;
import kr.spring.restaurant.service.RestaurantService;
import kr.spring.restaurant.vo.RestaurantVO;
import kr.spring.review.base.service.BaseReviewService;
import kr.spring.review.base.service.ReviewStatisticsService;
import kr.spring.review.base.vo.BaseReviewVO;
import kr.spring.review.base.vo.ReviewStatisticsVO;
import kr.spring.tour.service.TourService;
import kr.spring.tour.vo.TourVO;
import kr.spring.tour.vo.TouristAttractionVO;
import kr.spring.util.PagingUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/restaurant")
public class RestaurantController {

	@Autowired
	private RestaurantService restaurantService;

	@Autowired
	private BaseReviewService baseReviewService;

	@Autowired
	private ReviewStatisticsService reviewStatisticsService;

	@Autowired
	private TourService tourService;

	@GetMapping("")
	public String restaurantMain(@RequestParam(name = "pageNum", defaultValue="1") int pageNum,
			@RequestParam(name = "keyword", defaultValue = "") String keyword,
			Model model) {

		int count = restaurantService.selectRowCount();

		//페이지 처리
		PagingUtil page = new PagingUtil(null,keyword,
				pageNum,count,9,10,
				"");
		Map<String,Object> map = 
				new HashMap<String,Object>();
		List<TourVO> list = null;
		if(count > 0) {
			map.put("start", page.getStartRow());
			map.put("end", page.getEndRow());
			map.put("keyword", keyword);
			list = restaurantService.selectList(map);
		}
		model.addAttribute("keyword", keyword);
		model.addAttribute("count", count);
		model.addAttribute("list", list);
		model.addAttribute("page", page.getPage());
		return "views/restaurant/restaurant";
	}

	// 항목 자세히 보기
	@GetMapping("/detail")
	public String restaurantDetail(@RequestParam(name = "contentId") Long contentId, Model model) {
		// 식당 정보
		RestaurantVO vo = restaurantService.selectRestaurant(contentId);

		// api 정보 insert하고 select 하는 부분
		if(vo == null){
			vo = restaurantService.selectRestaurantWithApi(contentId);
		}

		// 식당별 리뷰 목록
		List<BaseReviewVO> reviewList = baseReviewService.getHouseReviews(contentId, 1, 10);
		// 식당별 리뷰 통계
		ReviewStatisticsVO status = reviewStatisticsService.getReviewStatisticsByHouse(contentId);

		model.addAttribute("info",vo);
		model.addAttribute("reviewList",reviewList);
		model.addAttribute("status", status);

		//api에서 정보를 뱉지않을때
		if(vo == null){
			// tour_content에서 기본 정보만 가져오기
			TourVO tourInfo = tourService.selectTourContent(contentId);
			if(tourInfo != null) {
				model.addAttribute("info", tourInfo);
				model.addAttribute("contentTypeName", "음식점");
				model.addAttribute("reviewActionUrl", "/restaurant/saveReview");
				// 기본 정보만 있을 때는 status와 reviewList를 null로 설정
				model.addAttribute("status", null);
				model.addAttribute("reviewList", null);
				return "views/common/content-detail-basic";
			}
		}

		return "views/restaurant/restaurant-detail";
	}

	// 리뷰 작성
	@PostMapping("/saveReview")
	public String saveReview(@ModelAttribute BaseReviewVO reviewDTO, HttpSession session) {
		
		MemberVO member = (MemberVO) session.getAttribute("member");
		reviewDTO.setMemberId(member.getMemberId());
		reviewDTO.setStatus(1);
		reviewDTO.setContentTypeId(39L); // 음식점 타입 ID

		baseReviewService.writeReview(reviewDTO);
		log.debug("<<리뷰 작성>> 사용자 id : {}", member.getMemberId());

		return "redirect:/restaurant/detail?contentId=" + reviewDTO.getContentId();
	}
}
