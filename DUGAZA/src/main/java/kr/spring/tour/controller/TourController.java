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
import kr.spring.tour.vo.ContentTypeAdd;
import kr.spring.tour.vo.TourVO;
import kr.spring.util.PagingUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/tour")
public class TourController {

	@Autowired
	private TourService tourService;

	// 관광지 메인 화면 호출
	@GetMapping("")
	public String tourMain(@RequestParam(name = "pageNum", defaultValue="1") int pageNum,
						   @RequestParam(name = "category", defaultValue="0") int category,
						   @RequestParam(name = "keyword", defaultValue = "") String keyword,
						   Model model) {
		int count = tourService.selectRowCount();

		//페이지 처리
		PagingUtil page = new PagingUtil(null,keyword,
				pageNum,count,9,10,
				"","&category="+category);
		Map<String,Object> map = 
				new HashMap<String,Object>();
		List<TourVO> list = null;
		if(count > 0) {
			map.put("start", page.getStartRow());
			map.put("end", page.getEndRow());
			map.put("category", category);
			map.put("keyword", keyword);
			list = tourService.selectList(map);
		}
		model.addAttribute("count", count);
		model.addAttribute("list", list);
		model.addAttribute("page", page.getPage());
		model.addAttribute("category", category);

		return "views/sample/tour";
	}
	
	// 항목 자세히 보기
	@GetMapping("/detail")
	public String tourDetail(@RequestParam(name = "id") Long id, Model model) {
	    int typeId = tourService.selectContentTypeId(id);
	    ContentTypeAdd contentType = ContentTypeAdd.fromId(typeId);

	    return "redirect:/" + contentType.getName() + "/detail?id=" + id;
	}

}
