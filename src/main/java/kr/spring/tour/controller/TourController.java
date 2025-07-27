package kr.spring.tour.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import kr.spring.tour.service.TourService;
import kr.spring.tour.vo.TourVO;
import kr.spring.util.CursorPagingUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/tour")
public class TourController {

	@Autowired
	private TourService tourService;

	// 관광지 메인 화면 호출 (커서 기반 페이지네이션)
	@GetMapping("")
	public String tourMain(
			@RequestParam(name = "cursor", required = false) Long cursor,
			@RequestParam(name = "pageSize", defaultValue = "9") int pageSize,
			@RequestParam(name = "category", defaultValue = "0") int category,
			@RequestParam(name = "keyword", defaultValue = "") String keyword,
			Model model) {
		
		log.info("관광지 메인 페이지 요청: cursor={}, pageSize={}, category={}, keyword={}", 
				cursor, pageSize, category, keyword);
		
		// 커서 기반 페이지네이션 유틸 생성
		CursorPagingUtil cursorPaging = new CursorPagingUtil(cursor, pageSize, keyword, String.valueOf(category));
		
		// 데이터 조회
		List<TourVO> list = tourService.selectListByCursor(cursorPaging);
		
		// 다음 페이지 존재 여부 확인
		boolean hasNext = tourService.hasNextPage(cursorPaging);
		cursorPaging.setHasNext(hasNext);
		
		// 다음 페이지 커서 계산 (마지막 항목의 ID)
		Long nextCursor = null;
		if (!list.isEmpty() && hasNext) {
			nextCursor = list.get(list.size() - 1).getContentId();
		}
		
		// 이전 페이지 커서 (현재 커서가 있으면 그대로 사용)
		Long prevCursor = cursor;
		
		// 페이지네이션 HTML 생성
		String paginationHtml = cursorPaging.generatePaginationHtml("/tour", nextCursor, prevCursor);
		
		model.addAttribute("list", list);
		model.addAttribute("keyword", keyword);
		model.addAttribute("category", category);
		model.addAttribute("pageSize", pageSize);
		model.addAttribute("hasNext", hasNext);
		model.addAttribute("nextCursor", nextCursor);
		model.addAttribute("prevCursor", prevCursor);
		model.addAttribute("pagination", paginationHtml);
		model.addAttribute("cursorPaging", cursorPaging);

		return "views/sample/tour";
	}
	
	// 항목 자세히 보기 - TouristAttractionController로 직접 연결하도록 변경
	@GetMapping("/detail")
	public String tourDetail(@RequestParam Long id, Model model) {
		return "redirect:/touristAttraction/detail?id=" + id;
	}
}
