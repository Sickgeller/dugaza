package kr.spring.community.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Slf4j
@Controller
@RequestMapping("/community")
public class CommunityController {
	@GetMapping("/main")
	public String communityMain() {
		return new String("views/sample/community");
	}
	
}
