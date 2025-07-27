package kr.spring.main.controller;

import kr.spring.common.RoleType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import kr.spring.auth.security.CustomUserDetails;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class MainController {
	
	@GetMapping("/")
	public String init(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
		// 로그아웃 성공 메시지 처리
		String logoutMessage = (String) model.getAttribute("logoutMessage");
		if (logoutMessage != null) {
			model.addAttribute("logoutMessage", logoutMessage);
		}
		
		// 관리자는 자동으로 관리자 페이지로 리다이렉트
		if(userDetails != null && userDetails.getAuthorities().stream()
				.anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {

			return "redirect:/admin";
		}
		return "views/index";
	}
	
	@GetMapping("/main/main")
	public String main(Model model) {
		return "views/sample/main";
	}
	
	@GetMapping("/common/terms")
	public String termsPage() {
	    return "views/terms/terms";
	}
    @GetMapping("/common/privacy")
    public String showPrivacyPage() {
        return "views/privacy/privacy";  
    }
    @GetMapping("/common/about")
    public String showAboutPage() {
        return "views/about/about"; 
    }
	
	@GetMapping("/accessDenied")
	public String access(Model model) {
		return "views/common/accessDenied";
	}
	
	@GetMapping("/fileSizeLimit")
	public String fileSizeLimit(Model model) {
		return "views/common/fileSizeLimit";
	}
}










