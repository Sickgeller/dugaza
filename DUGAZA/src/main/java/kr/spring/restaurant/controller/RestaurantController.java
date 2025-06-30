package kr.spring.restaurant.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/restaurant")
public class RestaurantController {
	
	@GetMapping("/main")
	public String restaurantMain() {
		return "views/sample/restaurant";
	}
}
