package kr.spring.tour.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import kr.spring.tour.service.TouristAttractionService;
import kr.spring.tour.vo.TouristAttractionVO;

@Controller
@RequestMapping("/touristAttraction")
public class TouristAttractionController {
	@Autowired
	private TouristAttractionService touristAttractionService;

	// 항목 자세히 보기
	@GetMapping("/detail")
	public String touristAttarctionDetail(@RequestParam(name = "id") Long id, Model model) {
		TouristAttractionVO vo = touristAttractionService.selectAttraction(id);

		model.addAttribute("info",vo);
		
		return "views/sample/tour-detail";
	}
}
