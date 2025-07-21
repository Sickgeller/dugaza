package kr.spring.admin.controller;

import kr.spring.admin.service.AdminService;
import kr.spring.house.service.HouseService;
import kr.spring.seller.vo.HouseSellerDetailVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Slf4j
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final HouseService houseService;

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
    public String sellers(
            @RequestParam(name = "page", defaultValue="1") int pageNum,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "sellerType", required = false) String sellerType,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "sort", required = false) String sort,
            Model model) {
        
        Map<String, Object> params = new HashMap<>();
        
        // 검색 조건 설정
        if (keyword != null && !keyword.trim().isEmpty()) {
            params.put("keyword", keyword.trim());
        }
        
        // 판매자 타입 필터 설정
        if (sellerType != null && !sellerType.trim().isEmpty()) {
            params.put("sellerType", sellerType);
        }
        
        // 상태 필터 설정
        if (status != null && !status.trim().isEmpty()) {
            params.put("status", status);
        }
        
        // 정렬 설정
        if (sort != null && !sort.trim().isEmpty()) {
            params.put("sort", sort);
        }
        
        List<Map<String, Object>> sellerList = adminService.getSellerList(params);
        model.addAttribute("sellerList", sellerList);
        model.addAttribute("keyword", keyword);
        model.addAttribute("sellerType", sellerType);
        model.addAttribute("status", status);
        model.addAttribute("sort", sort);
        model.addAttribute("currentPage", pageNum);
        
        return "views/admin/super-admin-sellers";
    }

    // 차량 관리
    @GetMapping("/cars")
    public String cars(
            @RequestParam(name = "page", defaultValue="1") int pageNum,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "carType", required = false) String carType,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "sort", required = false) String sort,
            Model model) {
        
        Map<String, Object> params = new HashMap<>();
        
        // 검색 조건 설정
        if (keyword != null && !keyword.trim().isEmpty()) {
            params.put("keyword", keyword.trim());
        }
        
        // 차량 타입 필터 설정
        if (carType != null && !carType.trim().isEmpty()) {
            params.put("carType", carType);
        }
        
        // 상태 필터 설정
        if (status != null && !status.trim().isEmpty()) {
            params.put("status", status);
        }
        
        // 정렬 설정
        if (sort != null && !sort.trim().isEmpty()) {
            params.put("sort", sort);
        }
        
        List<Map<String, Object>> carList = adminService.getCarList();
        model.addAttribute("carList", carList);
        model.addAttribute("keyword", keyword);
        model.addAttribute("carType", carType);
        model.addAttribute("status", status);
        model.addAttribute("sort", sort);
        model.addAttribute("currentPage", pageNum);
        
        return "views/admin/super-admin-cars";
    }
    
    // 차량 상세보기
    @GetMapping("/cars/{carId}/detail")
    @ResponseBody
    public Map<String, Object> getCarDetail(@PathVariable(name = "carId") Long carId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 차량 목록에서 해당 차량 찾기
            List<Map<String, Object>> carList = adminService.getCarList();
            Map<String, Object> carDetail = carList.stream()
                    .filter(car -> carId.equals(car.get("carId")))
                    .findFirst()
                    .orElse(null);
            
            if (carDetail != null) {
                response.put("success", true);
                response.put("car", carDetail);
            } else {
                response.put("success", false);
                response.put("message", "차량을 찾을 수 없습니다.");
            }
        } catch (Exception e) {
            log.error("차량 상세보기 실패: carId={}", carId, e);
            response.put("success", false);
            response.put("message", "차량 정보를 불러올 수 없습니다.");
        }
        
        return response;
    }
    
    // 차량 상태 수정
    @PostMapping("/cars/{carId}/status")
    @ResponseBody
    public Map<String, Object> updateCarStatus(@PathVariable Long carId, @RequestParam String status) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            adminService.updateProductStatus(carId, "CAR", status);
            response.put("success", true);
            response.put("message", "차량 상태가 성공적으로 변경되었습니다.");
        } catch (Exception e) {
            log.error("차량 상태 변경 실패: carId={}, status={}", carId, status, e);
            response.put("success", false);
            response.put("message", "차량 상태 변경에 실패했습니다.");
        }
        
        return response;
    }
    
    // 승인 대기 차량 목록 조회
    @GetMapping("/pending-cars")
    @ResponseBody
    public Map<String, Object> getPendingCars() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Map<String, Object>> pendingCars = adminService.getPendingCarList();
            response.put("success", true);
            response.put("cars", pendingCars);
            response.put("count", pendingCars.size());
        } catch (Exception e) {
            log.error("승인 대기 차량 목록 조회 실패", e);
            response.put("success", false);
            response.put("message", "승인 대기 차량 목록을 불러올 수 없습니다.");
        }
        
        return response;
    }
    
    // 차량 승인 처리
    @PostMapping("/cars/{carId}/approve")
    @ResponseBody
    public Map<String, Object> approveCar(@PathVariable Long carId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            adminService.updateProductStatus(carId, "CAR", "AVAILABLE");
            response.put("success", true);
            response.put("message", "차량이 성공적으로 승인되었습니다.");
        } catch (Exception e) {
            log.error("차량 승인 실패: carId={}", carId, e);
            response.put("success", false);
            response.put("message", "차량 승인에 실패했습니다.");
        }
        
        return response;
    }
    
    // 숙소 관리
    @GetMapping("/houses")
    public String houses(
            @RequestParam(name = "page", defaultValue="1") int pageNum,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "category", required = false) String category,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "sort", required = false) String sort,
            Model model) {
        
        Map<String, Object> params = new HashMap<>();
        
        // 검색 조건 설정
        if (keyword != null && !keyword.trim().isEmpty()) {
            params.put("keyword", keyword.trim());
        }
        
        // 카테고리 필터 설정
        if (category != null && !category.trim().isEmpty()) {
            params.put("category", category);
        }
        
        // 상태 필터 설정
        if (status != null && !status.trim().isEmpty()) {
            params.put("status", status);
        }
        
        // 정렬 설정
        if (sort != null && !sort.trim().isEmpty()) {
            params.put("sort", sort);
        }
        
        // 페이지네이션 설정
        int pageSize = 10;
        params.put("start", (pageNum - 1) * pageSize + 1);
        params.put("end", pageNum * pageSize);
        
        List<Map<String, Object>> houseList = adminService.getHouseList(params);
        int totalCount = adminService.getHouseCount(params);
        int totalPages = (int) Math.ceil((double) totalCount / pageSize);
        
        model.addAttribute("houseList", houseList);
        model.addAttribute("count", totalCount);
        model.addAttribute("currentPage", pageNum);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("keyword", keyword);
        model.addAttribute("category", category);
        model.addAttribute("status", status);
        model.addAttribute("sort", sort);
        
        return "views/admin/super-admin-accommodations";
    }
    
    // 숙소 상세보기
    @GetMapping("/houses/{houseId}/detail")
    @ResponseBody
    public Map<String, Object> getHouseDetail(@PathVariable(name = "houseId") Long houseId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            log.info("숙소 상세보기 요청: houseId={}", houseId);
            
            Map<String, Object> houseDetail = adminService.getHouseDetail(houseId);
            log.info("서비스에서 반환된 결과: {}", houseDetail != null ? "성공" : "실패");
            
            if (houseDetail != null) {
                response.put("success", true);
                response.put("house", houseDetail);
                log.info("숙소 상세보기 성공: houseId={}", houseId);
            } else {
                response.put("success", false);
                response.put("message", "상세정보를 제공하지 않는 숙소입니다.");
                log.warn("숙소 상세보기 실패: houseId={}", houseId);
            }
        } catch (Exception e) {
            log.error("숙소 상세보기 실패: houseId={}", houseId, e);
            response.put("success", false);
            response.put("message", "숙소 정보를 불러올 수 없습니다: " + e.getMessage());
        }
        
        return response;
    }
    
    // 숙소 상태 변경
    @PostMapping("/houses/status")
    @ResponseBody
    public Map<String, Object> updateHouseStatus(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long houseId = Long.valueOf(request.get("houseId").toString());
            String status = request.get("status").toString();
            
            adminService.updateProductStatus(houseId, "house", status);
            response.put("success", true);
            response.put("message", "숙소 상태가 성공적으로 변경되었습니다.");
        } catch (Exception e) {
            log.error("숙소 상태 변경 실패: request={}", request, e);
            response.put("success", false);
            response.put("message", "숙소 상태 변경에 실패했습니다.");
        }
        
        return response;
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
    
    // 회원 상세보기
    @GetMapping("/member/detail/{memberId}")
    @ResponseBody
    public Map<String, Object> getMemberDetail(@PathVariable(name = "memberId") Long memberId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Map<String, Object> memberDetail = adminService.getMemberDetail(memberId);
            response.put("success", true);
            response.put("member", memberDetail);
        } catch (Exception e) {
            log.error("회원 상세보기 실패: memberId={}", memberId, e);
            response.put("success", false);
            response.put("message", "회원 정보를 불러올 수 없습니다.");
        }
        
        return response;
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

    // 숙소 승인 관련 엔드포인트들
    @GetMapping("/pending-houses")
    @ResponseBody
    public Map<String, Object> getPendingHouses() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<HouseSellerDetailVO> pendingHouses = houseService.getPendingHouses();
            response.put("success", true);
            response.put("data", pendingHouses);
            response.put("count", pendingHouses.size());
        } catch (Exception e) {
            log.error("승인 대기 숙소 조회 중 오류 발생", e);
            response.put("success", false);
            response.put("message", "승인 대기 숙소 조회 중 오류가 발생했습니다.");
        }
        return response;
    }

    @PostMapping("/houses/{houseId}/approve")
    @ResponseBody
    public Map<String, Object> approveHouse(@PathVariable Long houseId, @RequestParam Long sellerId) {
        Map<String, Object> response = new HashMap<>();
        try {
            houseService.approveHouse(houseId, sellerId);
            response.put("success", true);
            response.put("message", "숙소가 승인되었습니다.");
        } catch (Exception e) {
            log.error("숙소 승인 중 오류 발생", e);
            response.put("success", false);
            response.put("message", "숙소 승인 중 오류가 발생했습니다.");
        }
        return response;
    }

    @PostMapping("/houses/{houseId}/reject")
    @ResponseBody
    public Map<String, Object> rejectHouse(@PathVariable Long houseId, @RequestParam Long sellerId) {
        Map<String, Object> response = new HashMap<>();
        try {
            houseService.rejectHouse(houseId, sellerId);
            response.put("success", true);
            response.put("message", "숙소가 거절되었습니다.");
        } catch (Exception e) {
            log.error("숙소 거절 중 오류 발생", e);
            response.put("success", false);
            response.put("message", "숙소 거절 중 오류가 발생했습니다.");
        }
        return response;
    }
} 