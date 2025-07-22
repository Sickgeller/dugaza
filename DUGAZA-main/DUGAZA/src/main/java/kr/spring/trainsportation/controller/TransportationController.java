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
		return "views/transportation/transportation";
	}
	
	@GetMapping("/train")
	public String trainForm() {
		return "views/transportation/train";
	}
	
	@GetMapping("/bus")
	public String busForm() { 
		return "views/transportation/bus";
	}
	
	
}
