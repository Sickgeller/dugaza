package kr.spring.seller.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import kr.spring.seller.service.SellerService;
import kr.spring.seller.vo.SellerVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/seller")
@Slf4j
public class SellerRestController {

    @Autowired
    private SellerService sellerService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/registerProc")
    public String submit(@Valid SellerVO sellerVO,
                         BindingResult result,
                         Model model,
                         HttpServletRequest request) {
        log.debug("<<회원가입>> : " + sellerVO);

        // 기본값 설정
        String encodedPassword = passwordEncoder.encode(sellerVO.getPassword());
        String role = "SELLER";
        String status = "ACTIVE";
        String sellerType = (sellerVO.getSellerType() == null || sellerVO.getSellerType().isEmpty()) 
            ? "GENERAL" : sellerVO.getSellerType();
        
        // Builder 패턴으로 SellerVO 객체 생성
        SellerVO newSeller = SellerVO.builder()
                .id(sellerVO.getId())
                .password(encodedPassword)
                .name(sellerVO.getName())
                .businessName(sellerVO.getBusinessName())
                .email(sellerVO.getEmail())
                .license(sellerVO.getLicense())
                .sellerType(sellerType)
                .status(status)
                .address(sellerVO.getAddress())
                .addressDetail(sellerVO.getAddressDetail() != null ? sellerVO.getAddressDetail() : "")
                .phone(sellerVO.getPhone())
                .secondaryPhone(sellerVO.getSecondaryPhone() != null ? sellerVO.getSecondaryPhone() : "")
                .role(role)
                .build();
        
        sellerService.register(newSeller);

        model.addAttribute("accessTitle", "회원가입");
        model.addAttribute("accessMsg",
                "회원가입이 완료되었습니다.");
        model.addAttribute("accessBtn", "홈으로");
        model.addAttribute("accessUrl",
                request.getContextPath()+"/");
        return "views/common/resultView";
    }

}
