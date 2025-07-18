package kr.spring.qnaResponse.controller;



import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import kr.spring.auth.security.CustomUserDetails;
import kr.spring.qnaQuestion.vo.QnaQuestionVO;
import kr.spring.qnaResponse.service.QnaResponseService;
import kr.spring.qnaResponse.vo.QnaResponseVO;
import lombok.extern.slf4j.Slf4j;



@Slf4j
@Controller
@RequestMapping("/admin/qna")
@PreAuthorize("hasRole('ROLE_ADMIN')") // 전체 접근 제한
public class QnaResponseController {

    @Autowired
    private QnaResponseService qnaResponseService;

    @GetMapping("/list")
    public String adminQnaList(Model model) {
        List<QnaQuestionVO> qnaList = qnaResponseService.getAllQnaList(); // 모든 문의
        model.addAttribute("qnaList", qnaList);
        return "qnaResponse/list"; // templates/admin/qna/list.html
    }
    
    @GetMapping("/detail/{qnaId}")
    public String getQnaDetail(@PathVariable("qnaId") Long qnaId, Model model) {
        // 문의 내용
        QnaQuestionVO qna = qnaResponseService.getQnaById(qnaId); 
        model.addAttribute("question", qna); // 이름을 "question"으로 바꿈
        model.addAttribute("response", qnaResponseService.getAnswerByQnaId(qnaId));

        // 기존 답변 (있으면)
        QnaResponseVO response = qnaResponseService.getAnswerByQnaId(qnaId); 
        model.addAttribute("response", response);

        return "qnaResponse/detail"; // templates/qnaResponse/detail.html
    }

    
    public String submitResponse(QnaResponseVO responseVO) {
        // 답변 저장
        qnaResponseService.insertResponse(responseVO);

        // 문의 상태 업데이트
        qnaResponseService.updateIsAnswered(responseVO.getQna_id(), "Y");

        return "redirect:/admin/qna/detail/" + responseVO.getQna_id();
    }
    
    
    public String adminQnaDetail(@PathVariable Long qnaId, Model model) {
        QnaQuestionVO question = qnaResponseService.getQnaById(qnaId); // 문의 내용
        QnaResponseVO response = qnaResponseService.getAnswerByQnaId(qnaId); // 기존 답변 (있다면)

        model.addAttribute("question", question);
        model.addAttribute("response", response != null ? response : new QnaResponseVO());

        return "qnaResponse/detail"; // templates/qnaResponse/detail.html
    }

    @PostMapping("/response")
    public String insertOrUpdateResponse(@ModelAttribute QnaResponseVO responseVO, 
            @AuthenticationPrincipal CustomUserDetails principalDetails) {

        Long memberId = principalDetails.getMember().getMemberId();
        responseVO.setMember_id(memberId);

        // 👉 여기에 로그 넣기
        System.out.println(">> qna_id: " + responseVO.getQna_id());
        System.out.println(">> content: " + responseVO.getContent());

        if (qnaResponseService.getAnswerByQnaId(responseVO.getQna_id()) == null) {
        	qnaResponseService.insertResponse(responseVO);
        	qnaResponseService.updateIsAnswered(responseVO.getQna_id(), "Y");
        } else {
            qnaResponseService.updateResponse(responseVO);
        }

        System.out.println(">> 저장할 responseVO: " + responseVO);  // Lombok @ToString 이용
        // 여기서 상태도 업데이트
        qnaResponseService.updateIsAnswered(responseVO.getQna_id(), "Y");

        return "redirect:/admin/qna/list";
    }


    
}
