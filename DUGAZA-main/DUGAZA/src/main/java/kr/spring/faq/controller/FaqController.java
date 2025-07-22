package kr.spring.faq.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletRequest;
import kr.spring.faq.service.FaqService;
import kr.spring.faq.vo.FaqVO;

@Controller
public class FaqController {
	@Autowired
	private FaqService faqService;

	@GetMapping("/faq")
	public String faqPage(Model model,HttpServletRequest request) {
		CsrfToken csrf = (CsrfToken) request.getAttribute("_csrf");
	    if (csrf != null) {
	        model.addAttribute("_csrf", csrf);
	    }

	    // 기존 FAQ 목록 데이터도 여기에 추가
	    List<FaqVO> faq = faqService.getFaqList();
	    model.addAttribute("faq", faq);

	    return "views/faq/faq";
	}
}
