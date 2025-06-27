package kr.spring.main.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import kr.spring.member.vo.PrincipalDetails;
import kr.spring.member.vo.UserRole;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class MainController {
	@GetMapping("/")
	public String init(@AuthenticationPrincipal
			           PrincipalDetails principal) {
		if(principal!=null
				&& principal.getMemberVO()
				            .getAuthority().equals(
						  UserRole.ADMIN.getValue())) {
			return "redirect:/main/admin";
		}
		return "redirect:/main/main";
	}
	
	@GetMapping("/main/main")
	public String main(Model model) {
		return "views/sample/index";
	}
	
	//관리자 페이지
	@GetMapping("/main/admin")
	public String adminMain(Model model) {
		return "views/main/admin";
	}
	
	@GetMapping("/accessDenied")
	public String access() {
		return "views/common/accessDenied";
	}
	
	@GetMapping("/fileSizeLimit")
	public String fileSizeLimit() {
		return "views/common/fileSizeLimit";
	}
	
}










