package kr.spring.seller.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import kr.spring.aop.LogExecutionTime;
import kr.spring.common.SellerType;
import kr.spring.auth.security.CustomUserDetails;
import kr.spring.seller.service.SellerService;
import kr.spring.seller.vo.SellerVO;
import kr.spring.util.ValidationUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
@RequestMapping("/seller")
public class SellerController {

    @Autowired
    private SellerService sellerService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/register")
    public String registerForm(){
        return "views/seller/register";
    }

    @PostMapping("/registerSeller")
    public String submit(@Valid SellerVO sellerVO,
                         BindingResult result,
                         Model model,
                         HttpServletRequest request) {
        if(result.hasErrors()) {
            ValidationUtil.printErrorFields(result);
            return registerForm();
        }

        sellerVO.setPassword(passwordEncoder.encode(sellerVO.getPassword()));
        sellerService.register(sellerVO);

        model.addAttribute("accessTitle", "회원가입 완료");
        model.addAttribute("accessMsg",
                "회원가입이 완료되었습니다. 이제 로그인하여 서비스를 이용해보세요!");
        model.addAttribute("accessBtn", "로그인하러 가기");
        model.addAttribute("accessUrl",
                request.getContextPath()+"/seller/login");

        return "views/common/resultView";
    }

    @GetMapping("/login")
    public String login(){
        return "views/seller/login";
    }

    /**
     * 판매자 메인 페이지 - 판매자 타입에 따라 다른 페이지로 연결
     * @param model 모델 객체
     * @return 판매자 타입에 따른 뷰 페이지
     */
    @GetMapping("/")
    public String main(Model model){
        return sellerMain(model);
    }

    @GetMapping("/main")
    public String sellerMain(Model model) {
        try {
            // 현재 인증된 사용자 정보 가져오기
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
                CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
                
                if (userDetails.isSeller()) {
                    SellerVO seller = userDetails.getSeller();
                    String sellerType = seller.getSellerType();
                    
                    log.info("판매자 로그인 - 사용자: {}, 타입: {}", userDetails.getUsername(), sellerType);
                    
                    // 모델에 사용자 정보 추가
                    model.addAttribute("userDetails", userDetails);
                    model.addAttribute("seller", seller);
                    
                    // 판매자 타입에 따라 다른 페이지로 연결
                    if (SellerType.CAR.getValue().equals(sellerType)) {
                        log.info("렌터카 판매자 페이지로 연결 - 사용자: {}", userDetails.getUsername());
                        return "views/seller/car-seller"; // 1번 HTML
                    } else if (SellerType.HOUSE.getValue().equals(sellerType)) {
                        log.info("숙소 판매자 페이지로 연결 - 사용자: {}", userDetails.getUsername());
                        return "views/seller/house-seller"; // 2번 HTML
                    } else {
                        log.warn("알 수 없는 판매자 타입: {} - 사용자: {}", sellerType, userDetails.getUsername());
                        model.addAttribute("error", "알 수 없는 판매자 타입입니다.");
                        return "views/common/error";
                    }
                } else {
                    log.warn("판매자가 아닌 사용자가 접근했습니다.");
                    model.addAttribute("error", "판매자만 접근할 수 있습니다.");
                    return "views/common/error";
                }
            } else {
                log.warn("인증된 판매자 정보를 찾을 수 없습니다.");
                model.addAttribute("error", "판매자 정보를 찾을 수 없습니다.");
                return "views/common/error";
            }
            
        } catch (Exception e) {
            log.error("판매자 메인 페이지 처리 중 오류 발생", e);
            model.addAttribute("error", "페이지 처리 중 오류가 발생했습니다.");
            return "views/common/error";
        }
    }

    /**
     * 판매자 대시보드 (기본 페이지)
     * @return 기본 판매자 페이지
     */
    @GetMapping("/dashboard")
    public String sellerDashboard() {
        return "views/seller/seller-dashboard";
    }

    /**
     * 판매자 프로필 페이지
     * @return 판매자 프로필 페이지
     */
    @GetMapping("/profile")
    public String sellerProfile() {
        return "views/seller/seller-profile";
    }

    /**
     * 판매자 설정 페이지
     * @return 판매자 설정 페이지
     */
    @GetMapping("/settings")
    public String sellerSettings() {
        return "views/seller/seller-settings";
    }
}
