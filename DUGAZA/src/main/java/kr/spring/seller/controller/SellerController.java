package kr.spring.seller.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import kr.spring.auth.security.CustomUserDetails;
import kr.spring.common.SellerType;
import kr.spring.reservation.service.HouseReservationService;
import kr.spring.review.base.service.BaseReviewService;
import kr.spring.room.service.RoomService;
import kr.spring.seller.service.SellerService;
import kr.spring.seller.vo.SellerVO;
import kr.spring.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.spring.house.vo.HouseVO;
import kr.spring.house.service.HouseService;
import kr.spring.util.PagingUtil;

@Controller
@Slf4j
@RequestMapping("/seller")
@RequiredArgsConstructor
public class SellerController {

    private final SellerService sellerService;
    private final RoomService roomService;
    private final HouseReservationService houseReservationService;
    private final PasswordEncoder passwordEncoder;
    private final BaseReviewService baseReviewService;
    private final SellerHouseController sellerHouseController;
    private final HouseService houseService;


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
            SellerVO seller = (SellerVO) model.getAttribute("seller");
            if (seller != null) {
                String sellerType = seller.getSellerType();
                log.info("판매자 로그인 - 사용자: {}, 타입: {}", seller.getName(), sellerType);
                if (SellerType.CAR.getValue().equals(sellerType)) {
                    log.info("렌터카 판매자 페이지로 연결 - 사용자: {}", seller.getName());
                    return "views/seller/car-seller";
                } else if (SellerType.HOUSE.getValue().equals(sellerType)) {
                    log.info("숙소 판매자 페이지로 연결 - 사용자: {}", seller.getName());
                    return "views/seller/house-seller";
                } else {
                    log.warn("알 수 없는 판매자 타입: {} - 사용자: {}", sellerType, seller.getName());
                    model.addAttribute("error", "알 수 없는 판매자 타입입니다.");
                    return "views/common/error";
                }
            } else {
                log.warn("판매자가 아닌 사용자가 접근했습니다.");
                model.addAttribute("error", "판매자만 접근할 수 있습니다.");
                return "views/common/error";
            }
        } catch (Exception e) {
            log.error("판매자 메인 페이지 처리 중 오류 발생", e);
            model.addAttribute("error", "페이지 처리 중 오류가 발생했습니다.");
            return "views/common/error";
        }
    }

    @GetMapping("/dashboard")
    public String myPage(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        SellerVO seller = userDetails.getSeller();
        if (seller == null) {
            return "redirect:/seller/login";
        }
        String sellerType = seller.getSellerType();
        if (SellerType.HOUSE.getValue().equals(sellerType)) {
            return "redirect:/seller/house/";
        } else if (SellerType.CAR.getValue().equals(sellerType)) {
            return "redirect:/seller/car/";
        }
        // 기타 사업자 유형 처리
        return "redirect:/seller/house/";
    }

    @GetMapping("/profile")
    public String sellerProfile() {
        return "views/seller/profile";
    }

    @GetMapping("/settings")
    public String sellerSettings() {
        return "views/seller/settings";
    }
}
