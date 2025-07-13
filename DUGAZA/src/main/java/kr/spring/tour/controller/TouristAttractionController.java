package kr.spring.tour.controller;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import kr.spring.review.base.service.BaseReviewService;
import kr.spring.review.base.service.ReviewStatisticsService;
import kr.spring.review.base.vo.BaseReviewVO;
import kr.spring.review.base.vo.ReviewStatisticsVO;
import kr.spring.tour.service.TourService;
import kr.spring.tour.service.TouristAttractionService;
import kr.spring.tour.vo.TouristAttractionVO;
import kr.spring.tour.vo.TourVO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/touristAttraction")
@RequiredArgsConstructor
public class TouristAttractionController {
	private final TouristAttractionService touristAttractionService;
	private final BaseReviewService baseReviewService;
	private final ReviewStatisticsService reviewStatisticsService;
	private final TourService tourService;

	// 여행명소 메인 화면 호출
	@GetMapping("")
	public String touristAttractionMain(@RequestParam(name = "pageNum", defaultValue="1") int pageNum,
						   @RequestParam(defaultValue = "") String keyword,
						   Model model) {
		int count = tourService.selectRowCount();

		//페이지 처리
		kr.spring.util.PagingUtil page = new kr.spring.util.PagingUtil(null,keyword,
				pageNum,count,9,10,
				"");
		java.util.Map<String,Object> map = 
				new java.util.HashMap<String,Object>();
		java.util.List<TourVO> list = null;
		if(count > 0) {
			map.put("start", page.getStartRow());
			map.put("end", page.getEndRow());
			map.put("keyword", keyword);
			list = tourService.selectList(map);
		}
		model.addAttribute("count", count);
		model.addAttribute("list", list);
		model.addAttribute("page", page.getPage());

		return "views/sample/tour";
	}

	// 항목 자세히 보기
	@GetMapping("/detail")
	public String touristAttractionDetail(@RequestParam(name = "contentId") Long contentId, Model model) {
		// 관광지 정보 (조인으로 기본 정보 + 상세 정보 모두 가져옴)
		TouristAttractionVO vo = touristAttractionService.selectAttraction(contentId);

		// 상세 정보가 없으면 API에서 가져와서 저장
		if(vo == null){
			vo = touristAttractionService.selectAttractionWithApi(contentId);
		}

		// 여전히 없으면 기본 정보만 가져오기
		if(vo == null){
			TourVO tourInfo = tourService.selectTourContent(contentId);
			if(tourInfo != null) {
				model.addAttribute("info", tourInfo);
				model.addAttribute("contentTypeName", "관광지");
				model.addAttribute("reviewActionUrl", "/touristAttraction/saveReview");
				// 기본 정보만 있을 때는 status를 null로 설정
				model.addAttribute("status", null);
				model.addAttribute("reviewList", null);
				return "views/common/content-detail-basic";
			}
			// 기본 정보도 없으면 null로 설정
			model.addAttribute("info", null);
		} else {
			// 정보가 있으면 모델에 추가
			model.addAttribute("info", vo);
		}

		// 관광지별 리뷰 목록
		List<BaseReviewVO> reviewList = baseReviewService.getHouseReviews(contentId, 1, 10);
		// 관광지별 리뷰 통계
		ReviewStatisticsVO status = reviewStatisticsService.getReviewStatisticsByHouse(contentId);

		model.addAttribute("reviewList", reviewList);
		model.addAttribute("status", status);

		return "views/tourist-attraction/tourist-attraction-detail";
	}
}
