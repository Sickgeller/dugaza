package kr.spring.tour.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/tour")
public class TourController {
	
	// 관광지 메인 화면 호출
	@GetMapping("/main")
	public String tourMain() {
		return "views/sample/tour";
	}
}
