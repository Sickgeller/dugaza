package kr.spring.tour.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import kr.spring.tour.service.CulturalCenterService;
import kr.spring.tour.vo.CulturalCenterVO;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@Controller
@RequestMapping("/culturalCenter")
public class CulturalCenterController {

	@Autowired
	private CulturalCenterService culturalCenterService;

	// 항목 자세히 보기
	@GetMapping("/detail")
	public String culturalCenterDetail(@RequestParam Long id, Model model) {
		CulturalCenterVO vo = culturalCenterService.selectCenter(id);
		
		return "views/sample/tour-detail";
	}
}
