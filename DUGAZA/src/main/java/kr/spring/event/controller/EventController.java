package kr.spring.event.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import kr.spring.event.service.EventService;
import kr.spring.event.vo.EventVO;
import kr.spring.tour.vo.TourVO;
import kr.spring.util.PagingUtil;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequestMapping("/event")
public class EventController {
	
	@Autowired
	private EventService eventService;
	
	@GetMapping("")
	public String eventtMain(@RequestParam(defaultValue="1") int pageNum,
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
	public String eventtDetail(@RequestParam Long id, Model model) {
		EventVO vo = eventService.selectEvent(id);

		model.addAttribute("info",vo);

		return "views/event/event-detail";
	}
}
