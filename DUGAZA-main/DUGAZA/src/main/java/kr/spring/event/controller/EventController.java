package kr.spring.event.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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
public class EventController {

	@Autowired
	private EventService eventService;

	@Autowired
	private BaseReviewService baseReviewService;

	@Autowired
	private ReviewStatisticsService reviewStatisticsService;

	@GetMapping("")
	public String eventtMain(@RequestParam(name = "pageNum", defaultValue="1") int pageNum,
			@RequestParam(defaultValue = "") String keyword,
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
			list = eventService.selectList(map);
		}
		model.addAttribute("count", count);
		model.addAttribute("list", list);
		model.addAttribute("page", page.getPage());
		return "views/event/event";
	}

	// 항목 자세히 보기
	@GetMapping("/detail")
	public String eventtDetail(@RequestParam(name = "contentId") Long contentId, Model model) {
		EventVO vo = eventService.selectEvent(contentId);
		// 행사별 리뷰 목록
		List<BaseReviewVO> reviewList = baseReviewService.getHouseReviews(contentId, 1, 10);
		// 행사별 리뷰 통계
		ReviewStatisticsVO status = reviewStatisticsService.getReviewStatisticsByHouse(contentId);

		model.addAttribute("info",vo);
		model.addAttribute("reviewList",reviewList);
		model.addAttribute("status", status);

		model.addAttribute("info",vo);

		return "views/event/event-detail";
	}

	// 리뷰 작성
	@PostMapping("/saveReview")
	public String saveReview(@ModelAttribute BaseReviewVO reviewDTO, HttpSession session) {

		MemberVO member = (MemberVO) session.getAttribute("member");
		reviewDTO.setMemberId(member.getMemberId());
		reviewDTO.setStatus(1);
		reviewDTO.setContentTypeId(15L); // 음식점 타입 ID

		baseReviewService.writeReview(reviewDTO);
		log.debug("<<리뷰 작성>> 사용자 id : {}", member.getMemberId());

		return "redirect:/event/detail?contentId=" + reviewDTO.getContentId();
	}
}
