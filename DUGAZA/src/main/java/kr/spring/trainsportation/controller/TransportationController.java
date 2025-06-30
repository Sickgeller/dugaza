package kr.spring.trainsportation.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/transportation")
public class TransportationController {
	@GetMapping("")
	public String transportationMain() {
		return "views/sample/transportation";
	}
}
