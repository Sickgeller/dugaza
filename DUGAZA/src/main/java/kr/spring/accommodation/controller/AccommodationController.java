package kr.spring.accommodation.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/accommodation")
public class AccommodationController {
	@GetMapping("/main")
	public String accommodationMain() {
		return "views/sample/accommodation";
	}
}
