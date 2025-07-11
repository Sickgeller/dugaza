package kr.spring.aop;

import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;
import kr.spring.member.vo.MemberVO;
import kr.spring.seller.vo.SellerVO;
import kr.spring.auth.security.CustomUserDetails;


@ControllerAdvice
public class GlobalModelAdvice {
    @ModelAttribute
    public void addModelMemberAndSeller(
            Model model,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        if(userDetails != null) {
            if(userDetails.getSeller() != null) {
                model.addAttribute("seller", userDetails.getSeller());
            }
            if(userDetails.getMember() != null) {
                model.addAttribute("member", userDetails.getMember());
            }
        }
    }
} 