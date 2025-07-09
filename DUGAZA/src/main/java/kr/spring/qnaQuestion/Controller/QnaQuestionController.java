package kr.spring.qnaQuestion.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import kr.spring.auth.security.PrincipalDetails;
import kr.spring.qnaQuestion.service.QnaQuestionService;
import kr.spring.qnaQuestion.vo.QnaQuestionVO;
import kr.spring.util.ValidationUtil;

@Controller
public class QnaQuestionController {
	
	@Autowired
	private QnaQuestionService qnaQuestionService;

	@ModelAttribute
	public QnaQuestionVO initCommand() {
	    return new QnaQuestionVO();
	}

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/faq/qnaQuestion")
	public String submitInquiry(@Valid QnaQuestionVO qnaVO,
	                            BindingResult result,
	                            @AuthenticationPrincipal PrincipalDetails principal,
	                            HttpServletRequest request,
	                            Model model) {
	    if (result.hasErrors()) {
	        ValidationUtil.printErrorFields(result);
	        model.addAttribute("message", "입력값이 유효하지 않습니다.");
	        model.addAttribute("url", request.getContextPath() + "/faq");
	        return "views/common/resultAlert";
	    }

	    qnaVO.setMemberId(principal.getMemberVO().getMemberId()); // 로그인 사용자 ID
	    qnaQuestionService.insertQuestion(qnaVO); // DB insert

	    model.addAttribute("message", "문의가 등록되었습니다.");
	    model.addAttribute("url", request.getContextPath() + "/faq");

	    return "views/common/resultAlert";
	}

	
}
