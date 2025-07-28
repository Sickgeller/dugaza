package kr.spring.event.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.spring.auth.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import kr.spring.event.service.EventService;
import kr.spring.event.vo.EventVO;
import kr.spring.member.vo.MemberVO;
import kr.spring.review.base.service.BaseReviewService;
import kr.spring.review.base.service.ReviewStatisticsService;
import kr.spring.review.base.vo.BaseReviewVO;
import kr.spring.review.base.vo.ReviewStatisticsVO;
import kr.spring.tour.vo.TourVO;
import kr.spring.util.PagingUtil;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequestMapping("/event")
@RequiredArgsConstructor
public class EventController {

	private final EventService eventService;
	private final BaseReviewService baseReviewService;
	private final ReviewStatisticsService reviewStatisticsService;

	@GetMapping("")
	public String eventtMain(@RequestParam(name = "pageNum", defaultValue="1") int pageNum,
			@RequestParam(name = "keyword", defaultValue = "") String keyword,
			Model model) {

		int count = eventService.selectRowCount();

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
			map.put("pageSize", 9); // 명확히 int 타입으로 추가
			list = eventService.selectList(map);
		}
		model.addAttribute("keyword", keyword);
		model.addAttribute("count", count);
		model.addAttribute("list", list);
		model.addAttribute("page", page.getPage());
		return "views/event/event";
	}

	// 항목 자세히 보기
	@GetMapping("/detail")
	public String eventDetail(@RequestParam(name = "contentId") Long contentId, Model model) {
		// 행사 정보
		EventVO vo = eventService.selectEvent(contentId);

		// api 정보 insert하고 select 하는 부분
		if(vo == null){
			vo = eventService.selectEventWithApi(contentId);
		}

		// 행사별 리뷰 목록
		List<BaseReviewVO> reviewList = baseReviewService.getHouseReviews(contentId, 1, 10);
		// 행사별 리뷰 통계
		ReviewStatisticsVO status = reviewStatisticsService.getReviewStatisticsByHouse(contentId);

		model.addAttribute("info",vo);
		model.addAttribute("reviewList",reviewList);
		model.addAttribute("status", status);

		//api에서 정보를 뱉지않을때
		if(vo.getContentId() == null){
			// tour_content에서 기본 정보만 가져오기
			TourVO tourInfo = eventService.selectTourContent(contentId);
			if(tourInfo != null) {
				model.addAttribute("info", tourInfo);
				model.addAttribute("contentTypeName", "행사");
				model.addAttribute("reviewActionUrl", "/event/saveReview");
				// 기본 정보만 있을 때는 status와 reviewList를 null로 설정
				model.addAttribute("status", null);
				model.addAttribute("reviewList", null);
				return "views/common/content-detail-basic";
			}
		}

		return "views/event/event-detail";
	}

	// 리뷰 작성
	@PostMapping("/saveReview")
	public String saveReview(
			@ModelAttribute BaseReviewVO reviewDTO,
			@AuthenticationPrincipal CustomUserDetails userDetails) {
		MemberVO member = userDetails.getMember();
		reviewDTO.setMemberId(member.getMemberId());
		reviewDTO.setStatus(1);
		reviewDTO.setContentTypeId(15L); // 음식점 타입 ID

		baseReviewService.writeReview(reviewDTO);
		log.debug("<<리뷰 작성>> 사용자 id : {}", member.getMemberId());

		return "redirect:/event/detail?contentId=" + reviewDTO.getContentId();
	}
}
