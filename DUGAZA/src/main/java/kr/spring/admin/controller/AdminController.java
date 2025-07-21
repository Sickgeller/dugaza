package kr.spring.admin.controller;

import kr.spring.admin.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    // 대시보드
    @GetMapping("")
    public String dashboard(Model model) {
        Map<String, Object> stats = adminService.getDashboardStats();
        model.addAttribute("stats", stats);
        return "views/admin/admin";
    }

    // 대시보드 (명시적 경로)
    @GetMapping("/dashboard")
    public String dashboardExplicit(Model model) {
        return dashboard(model);
    }

    // 판매자 관리
    @GetMapping("/sellers")
    public String sellers(Model model) {
        List<Map<String, Object>> sellerList = adminService.getSellerList();
        model.addAttribute("sellerList", sellerList);
        return "views/admin/super-admin-sellers";
    }

    // 차량 관리
    @GetMapping("/cars")
    public String cars(Model model) {
        List<Map<String, Object>> carList = adminService.getCarList();
        model.addAttribute("carList", carList);
        return "views/admin/super-admin-cars";
    }
    
    // 숙소 관리
    @GetMapping("/houses")
    public String houses(Model model) {
        List<Map<String, Object>> houseList = adminService.getHouseList();
        model.addAttribute("houseList", houseList);
        return "views/admin/super-admin-accommodations";
    }
    
    // 리뷰 관리
    @GetMapping("/reviews")
    public String reviews(Model model) {
        List<Map<String, Object>> reviewList = adminService.getReviewList();
        model.addAttribute("reviewList", reviewList);
        return "views/admin/super-admin-reviews";
    }
    
    // 회원 상태 업데이트
    @PostMapping("/member/status")
    @ResponseBody
    public String updateMemberStatus(@RequestParam(name = "memberId") Long memberId, @RequestParam(name = "status") String status) {
        try {
            adminService.updateMemberStatus(memberId, status);
            return "success";
        } catch (Exception e) {
            log.error("회원 상태 업데이트 실패: memberId={}, status={}", memberId, status, e);
            return "error";
        }
    }
    
    // 판매자 상태 업데이트
    @PostMapping("/seller/status")
    @ResponseBody
    public String updateSellerStatus(@RequestParam(name = "sellerId") Long sellerId, @RequestParam(name = "status") String status) {
        try {
            adminService.updateSellerStatus(sellerId, status);
            return "success";
        } catch (Exception e) {
            log.error("판매자 상태 업데이트 실패: sellerId={}, status={}", sellerId, status, e);
            return "error";
        }
    }

    // 리뷰 상태 수정
    @PostMapping("/review/status")
    @ResponseBody
    public String updateReviewStatus(@RequestParam(name = "reviewId") Long reviewId, @RequestParam(name = "status") String status) {
        try {
            adminService.updateReviewStatus(reviewId, status);
            return "success";
        } catch (Exception e) {
            log.error("리뷰 상태 수정 실패", e);
            return "error";
        }
    }

    // 상품 상태 수정
    @PostMapping("/product/status")
    @ResponseBody
    public String updateProductStatus(@RequestParam(name = "productId") Long productId, 
                                    @RequestParam(name = "productType") String productType, 
                                    @RequestParam(name = "status") String status) {
        try {
            adminService.updateProductStatus(productId, productType, status);
            return "success";
        } catch (Exception e) {
            log.error("상품 상태 수정 실패", e);
            return "error";
        }
    }



    // 문의 상태 수정
    @PostMapping("/inquiry/status")
    @ResponseBody
    public String updateInquiryStatus(@RequestParam(name = "inquiryId") Long inquiryId, @RequestParam(name = "status") String status) {
        try {
            adminService.updateInquiryStatus(inquiryId, status);
            return "success";
        } catch (Exception e) {
            log.error("문의 상태 수정 실패", e);
            return "error";
        }
    }
} 