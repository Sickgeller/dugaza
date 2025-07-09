package kr.spring.qnaQuestion.Controller;

import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import kr.spring.auth.security.CustomUserDetails;
import kr.spring.member.vo.MemberVO;
import kr.spring.qnaQuestion.service.QnaQuestionService;
import kr.spring.qnaQuestion.vo.QnaQuestionVO;
import kr.spring.util.ValidationUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class QnaQuestionController {

    @Autowired
    private QnaQuestionService qnaQuestionService;

    /** 기본 QnaQuestionVO 초기화 **/
    @ModelAttribute
    public QnaQuestionVO initCommand() {
        return new QnaQuestionVO();
    }

    /** FAQ 메인 페이지 **/
    public String faqMain(Model model, HttpServletRequest request) {
        CsrfToken token = (CsrfToken) request.getAttribute("_csrf");
        model.addAttribute("_csrf", token); // 수동 추가
        return "views/faq/faq";
    }

    /** 1:1 문의 등록 처리 **/
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/faq/qnaQuestion")
    public String submitInquiry(@Valid QnaQuestionVO qnaVO,
                                BindingResult result,
                                @AuthenticationPrincipal CustomUserDetails principal,
                                HttpServletRequest request,
                                Model model) {

    	MemberVO member = principal.getMember(); // 또는 getMember()
    	Long memberId = member.getMemberId(); // 실제 member_id 값
        // 로그인 정보 확인
        if (principal == null || principal.getMember() == null) {
            model.addAttribute("message", "로그인 후 이용 가능합니다.");
            model.addAttribute("url", request.getContextPath() + "/member/login");
            return "views/common/resultAlert";
        }

        // 입력값 검증
        if (result.hasErrors()) {
            ValidationUtil.printErrorFields(result);
            model.addAttribute("message", "입력값이 유효하지 않습니다.");
            model.addAttribute("url", request.getContextPath() + "/faq");
            return "views/common/resultAlert";
        }
        
        // 로그인한 사용자 정보로 memberId 세팅
        memberId = principal.getMember().getMemberId();
        qnaVO.setMember_id(memberId);
        
        qnaVO.setIs_answered("N");

        log.debug("<<로그인한 사용자>> : {}", principal.getUsername());
        log.debug("<<memberId>> : {}", memberId);

        // DB 저장
        qnaQuestionService.insertQuestion(qnaVO);

        // 성공 응답
        model.addAttribute("message", "문의가 등록되었습니다.");
        model.addAttribute("url", request.getContextPath() + "/faq");
        return "views/common/resultAlert";
    }
}
