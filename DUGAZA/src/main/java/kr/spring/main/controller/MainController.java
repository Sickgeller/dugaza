package kr.spring.main.controller;

import kr.spring.common.RoleType;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import kr.spring.auth.security.PrincipalDetails;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class MainController {
	
	@GetMapping("/")
	public String init(@AuthenticationPrincipal PrincipalDetails principal, Model model, HttpServletRequest request) {
		model.addAttribute("requestURI", request.getRequestURI());
		if(principal != null && principal.getMemberVO().getRole().equals(RoleType.ADMIN.toString())) { 
			return "redirect:/admin";
		}
		return "views/index";
	}
	
	@GetMapping("/main/main")
	public String main(Model model, HttpServletRequest request) {
		model.addAttribute("requestURI", request.getRequestURI());
		return "views/sample/index";
	}
	
	@GetMapping("/terms")
	public String termsPage() {
	    return "views/common/terms";
	}
	
	//관리자 페이지
	@GetMapping("/admin")
	public String adminMain(Model model, HttpServletRequest request) {
		model.addAttribute("requestURI", request.getRequestURI());
		return "views/sample/admin";
	}
	
	@GetMapping("/accessDenied")
	public String access(Model model, HttpServletRequest request) {
		model.addAttribute("requestURI", request.getRequestURI());
		return "views/common/accessDenied";
	}
	
	@GetMapping("/fileSizeLimit")
	public String fileSizeLimit(Model model, HttpServletRequest request) {
		model.addAttribute("requestURI", request.getRequestURI());
		return "views/common/fileSizeLimit";
	}
}










