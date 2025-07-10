package kr.spring.seller.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import kr.spring.common.SellerType;
import kr.spring.auth.security.CustomUserDetails;
import kr.spring.reservation.house.service.HouseReservationService;
import kr.spring.review.base.service.BaseReviewService;
import kr.spring.room.service.RoomService;
import kr.spring.seller.service.SellerService;
import kr.spring.seller.vo.SellerVO;
import kr.spring.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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

    /**
     * 모든 컨트롤러 메서드에서 자동으로 실행되어 인증된 판매자 정보를 모델에 추가
     */
    @ModelAttribute
    public void addSellerToModel(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() &&
            authentication.getPrincipal() instanceof CustomUserDetails) {
            
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            
            if (userDetails.isSeller()) {
                model.addAttribute("seller", userDetails.getSeller());
            }
        }
    }

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
            // @ModelAttribute에서 자동으로 추가된 seller 정보 사용
            SellerVO seller = (SellerVO) model.getAttribute("seller");
            
            if (seller != null) {
                String sellerType = seller.getSellerType();
                CustomUserDetails userDetails = (CustomUserDetails) model.getAttribute("userDetails");
                
                log.info("판매자 로그인 - 사용자: {}, 타입: {}", userDetails.getUsername(), sellerType);
                
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
            
        } catch (Exception e) {
            log.error("판매자 메인 페이지 처리 중 오류 발생", e);
            model.addAttribute("error", "페이지 처리 중 오류가 발생했습니다.");
            return "views/common/error";
        }
    }

    @GetMapping("/dashboard")
    public String myPage(Model model){
        // @ModelAttribute에서 자동으로 추가된 seller 정보 사용
        SellerVO seller = (SellerVO) model.getAttribute("seller");

        if(seller != null) {
            model.addAttribute("currentMenu", "dashboard");
            
            // sellerType null 체크 추가
            if(seller.getSellerType() == null) {
                log.error("판매자 타입이 null입니다. 사용자: {}, 판매자 정보: {}", seller.getName(), seller);
                model.addAttribute("error", "판매자 정보를 불러올 수 없습니다. 다시 로그인해주세요.");
                return "views/common/error";
            }
            
            log.info("판매자 대시보드 접근 - 사용자: {}, 타입: {}", seller.getName(), seller.getSellerType());
            
            if(SellerType.CAR.getValue().equals(seller.getSellerType())) {
                return "views/seller/car/car-seller-main";
            }else if(SellerType.HOUSE.getValue().equals(seller.getSellerType())) {
                return sellerHouseController.main(model, seller);
            }else {
                log.warn("알 수 없는 판매자 타입: {} - 사용자: {}", seller.getSellerType(), seller.getName());
                model.addAttribute("error", "알 수 없는 판매자 타입입니다.");
                return "views/common/error";
            }
        }else{
            return login();
        }
    }


//    /**
//     * 판매자 대시보드 (기본 페이지)
//     * @return 기본 판매자 페이지
//     */
//    @GetMapping("/dashboard")
//    public String sellerDashboard() {
//        return "views/seller/seller-dashboard";
//    }

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
