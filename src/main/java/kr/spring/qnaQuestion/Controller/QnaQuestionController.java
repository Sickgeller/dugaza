package kr.spring.qnaQuestion.Controller;

import java.util.List;

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
import kr.spring.qnaResponse.service.QnaResponseService;
import kr.spring.qnaResponse.vo.QnaResponseVO;
import kr.spring.util.ValidationUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class QnaQuestionController {

    @Autowired
    private QnaQuestionService qnaQuestionService;
    
    @Autowired
    private QnaResponseService qnaResponseService;

    // 기본 QnaQuestionVO 초기화
    @ModelAttribute
    public QnaQuestionVO initCommand() {
        return new QnaQuestionVO();
    }
    
    //기본 QnaResponseVO 초기화
    @ModelAttribute
    public QnaResponseVO initCommand2() {
    	return new QnaResponseVO();
    }

    // FAQ 메인 페이지 
    public String faqMain(Model model, HttpServletRequest request) {
        CsrfToken token = (CsrfToken) request.getAttribute("_csrf");
        model.addAttribute("_csrf", token); // 수동 추가
        return "views/faq/faq";
    }

    //1:1 문의 등록 처리 
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/faq/qnaQuestion")
    public String submitInquiry(@Valid QnaQuestionVO qnaVO,
                                BindingResult result,
                                @AuthenticationPrincipal CustomUserDetails principal,
                                HttpServletRequest request,
                                Model model) {

    	MemberVO member = principal.getMember(); // 또는 getMember()
    	Long memberId = member.getMemberId(); // 실제 member_id 값
    	qnaVO.setMember_id(memberId);
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
            model.addAttribute("url", request.getContextPath() + "/common/faq");
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
        model.addAttribute("url", request.getContextPath() + "/common/faq");
        return "views/common/resultAlert";
    }
    
    
    @GetMapping("/qna/list")
    public String qnaList(Model model) {
        List<QnaQuestionVO> list = qnaQuestionService.getQnaList();
        model.addAttribute("qnaList", list);
        return "qna/list"; // 뷰 파일 경로 (Thymeleaf 또는 JSP)
    }
    
    
    @GetMapping("/qna/my")
    public String myQnaList(Model model, @AuthenticationPrincipal CustomUserDetails principal) {
        Long member_id = principal.getMember().getMemberId();
        System.out.println(">> 로그인한 member_id = " + member_id); // ✅ 추가

        List<QnaQuestionVO> list = qnaQuestionService.getQnaListByMember(member_id);
        System.out.println(">> qnaList.size() = " + list.size()); // ✅ 추가

        model.addAttribute("qnaList", list);
        return "qna/list";
    }
    
    @GetMapping("/qna/detail/{qnaId}")
    public String qnaDetail(@PathVariable("qnaId") Long qna_id, Model model) {
        QnaQuestionVO question = qnaQuestionService.getQnaDetail(qna_id);
        QnaResponseVO response = qnaResponseService.getAnswerByQnaId(qna_id);
        model.addAttribute("qna", question);
        model.addAttribute("response",response);
        return "qna/detail"; // detail.html (또는 .jsp)
    }

    
    @PostMapping("/qna/answer")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String insertAnswer(@RequestParam Long qna_id,
                               @RequestParam String content,
                               @AuthenticationPrincipal CustomUserDetails principal) {
        QnaResponseVO response = new QnaResponseVO();
        response.setQna_id(qna_id);
        response.setContent(content);
        response.setMember_id(principal.getMember().getMemberId());

        qnaQuestionService.insertAnswer(response);  // DAO를 통해 insert 처리
        return "redirect:/qna/detail/" + qna_id;
    }
    
    @GetMapping("/detail/{qnaId}")
    public String getQnaDetail(@PathVariable("qnaId") Long qna_id, Model model) {
        // 1. 문의글 가져오기
        QnaQuestionVO question = qnaResponseService.getQnaById(qna_id);
        model.addAttribute("qna", question); // 이름 명확하게

        // 2. 답변 가져오기 (있으면 null 아님)
        QnaResponseVO response = qnaResponseService.getAnswerByQnaId(qna_id);
        System.out.println(">>> 관리자 답변 내용: " + response);  // null이면 mapper 문제
        model.addAttribute("response", response); // 중복 제거하고 한 번만 호출

        return "qna/detail"; 
    }
}
