package kr.spring.aop;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;
import kr.spring.member.vo.MemberVO;
import kr.spring.seller.vo.SellerVO;

@ControllerAdvice
public class GlobalModelAdvice {
    @ModelAttribute
    public void addModelMemberAndSeller(
            Model model,
            @AuthenticationPrincipal MemberVO member,
            @AuthenticationPrincipal SellerVO seller) {
        if(member != null){
            model.addAttribute("member", member);
        }else if(seller != null){
            model.addAttribute("seller", seller);
        }
    }
} 