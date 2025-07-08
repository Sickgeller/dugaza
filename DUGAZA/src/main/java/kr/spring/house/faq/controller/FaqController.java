package kr.spring.house.faq.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import kr.spring.house.faq.service.FaqService;
import kr.spring.house.faq.vo.FaqVO;

@Controller
public class FaqController {
	@Autowired
	private FaqService faqService;

	@GetMapping("/faq")
	public String faqPage(Model model) {
		List<FaqVO> faq = faqService.getFaqList();
		System.out.println("FAQ 개수 : " + faq.size());
		model.addAttribute("faq",faq);
		return "views/faq/faq";
	}
}
