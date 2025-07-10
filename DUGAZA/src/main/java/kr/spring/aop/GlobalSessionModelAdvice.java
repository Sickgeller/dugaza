package kr.spring.aop;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.ui.Model;
import kr.spring.member.vo.MemberVO;
import kr.spring.seller.vo.SellerVO;

@ControllerAdvice
public class GlobalSessionModelAdvice {
    @ModelAttribute
    public void addSessionMemberAndSeller(
        @SessionAttribute(value = "member", required = false) MemberVO member,
        @SessionAttribute(value = "seller", required = false) SellerVO seller,
        Model model
    ) {
        if (member != null) model.addAttribute("member", member);
        if (seller != null) model.addAttribute("seller", seller);
    }
} 