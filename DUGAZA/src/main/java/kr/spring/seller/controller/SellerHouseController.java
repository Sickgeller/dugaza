package kr.spring.seller.controller;

import kr.spring.auth.security.CustomUserDetails;
import kr.spring.common.SellerType;
import kr.spring.house.service.HouseService;
import kr.spring.reservation.house.service.HouseReservationService;
import kr.spring.reservation.house.vo.HouseReservationVO;
import kr.spring.review.house.service.HouseReviewService;
import kr.spring.review.house.vo.HouseReviewVO;
import kr.spring.review.house.service.ReviewStatisticsService;
import kr.spring.review.house.dto.ReviewStatisticsDto;
import kr.spring.room.dto.RoomDetailVO;
import kr.spring.room.service.RoomService;
import kr.spring.seller.service.SellerService;
import kr.spring.seller.vo.SellerVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@RequestMapping("/seller/house")
@Controller
@Slf4j
@RequiredArgsConstructor
public class SellerHouseController {

    private final SellerService sellerService;
    private final HouseService houseService;
    private final RoomService roomService;
    private final HouseReservationService houseReservationService;
    private final HouseReviewService houseReviewService;
    private final ReviewStatisticsService reviewStatisticsService;

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

    @GetMapping("/")
    public String main(Model model, SellerVO seller){
        try {
            if(seller.getSellerType().equals(SellerType.HOUSE.getValue())) {
                List<RoomDetailVO> roomList = roomService.getRoomsWithSeller(seller.getSellerId(),1,5);

                int totalRooms = roomList.size();
                int reservatedRooms = (int)roomList.stream().filter(room -> room.getStatus() == 1).count();
                int availableRooms = totalRooms - reservatedRooms;
                int reservedRate = totalRooms > 0 ? (int)((double)reservatedRooms / totalRooms * 100) : 0;

                // 대시보드 통계 데이터
                model.addAttribute("totalRooms", totalRooms);
                model.addAttribute("availableRooms", availableRooms);
                model.addAttribute("reservatedRooms", reservatedRooms);
                model.addAttribute("reservedRate", reservedRate);

                // 최근 예약 정보 (최대 5개)
                List<HouseReservationVO> reservationList = houseReservationService.getRecentlyReservations(seller.getSellerId());
                model.addAttribute("recentReservations", reservationList);

                // 최근 리뷰 정도 (최대 5개)
                List<HouseReviewVO> recentlyReviews = houseReviewService.getRecentlyReviews(seller.getSellerId());
                model.addAttribute("recentReviews", recentlyReviews);

                // 현재 메뉴 설정
                model.addAttribute("currentMenu", "dashboard");

                log.info("숙소 판매자 대시보드 - 총 객실: {}, 예약된 객실: {}, 예약률: {}%, 최근 예약: {}건",
                        totalRooms, reservatedRooms, reservedRate, reservationList.size());

                return "views/seller/house/house-seller-main";
            } else {
                log.warn("잘못된 판매자 타입: {}", seller.getSellerType());
                model.addAttribute("error", "숙소 판매자만 접근할 수 있습니다.");
                return "views/common/error";
            }
        } catch (Exception e) {
            log.error("숙소 판매자 대시보드 처리 중 오류 발생", e);
            model.addAttribute("error", "대시보드 정보를 불러올 수 없습니다.");
            return "views/common/error";
        }
    }

    @GetMapping("/management")
    public String rooms(Model model, @RequestParam(name = "page", defaultValue = "1") int page){
        SellerVO seller = (SellerVO) model.getAttribute("seller");
        
        // 페이징 처리를 위한 설정
        int pageSize = 10;
        int currentPage = Math.max(1, page);
        
        // 객실 목록 조회
        List<RoomDetailVO> rooms = roomService.getRoomsWithSeller(seller.getSellerId(), currentPage, pageSize);
        int totalRooms = roomService.getTotalRoomCount(seller.getSellerId());
        int availableRooms = (int)rooms.stream().filter(room -> room.getStatus() == 0).count();
        
        // 모델에 데이터 추가
        model.addAttribute("seller", seller);
        model.addAttribute("rooms", rooms);
        model.addAttribute("totalRooms", totalRooms);
        model.addAttribute("availableRooms", availableRooms);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", (int)Math.ceil((double)totalRooms / pageSize));
        model.addAttribute("currentMenu", "management");
        
        return "views/seller/house/house-seller-rooms";
    }

    @GetMapping("/reservation")
    public String reservation(Model model,
                              @RequestParam(name = "page", defaultValue = "1") int page,
                              @RequestParam(name = "pageSize", defaultValue = "10") int pageSize){
        SellerVO seller = (SellerVO) model.getAttribute("seller");

        // 페이징 처리를 위한 설정
        int startRow = (page - 1) * pageSize + 1;
        int endRow = page * pageSize;

        // 예약 목록 조회
        List<HouseReservationVO> reservations = houseReservationService.getReservations(seller.getSellerId(), startRow, endRow);
        
        // 최근 예약 목록 조회 (메인 대시보드용)
        List<HouseReservationVO> recentReservations = houseReservationService.getRecentlyReservations(seller.getSellerId());

        // 모델에 데이터 추가
        model.addAttribute("seller", seller);
        model.addAttribute("reservations", reservations);
        model.addAttribute("currentPage", page);
        model.addAttribute("currentMenu", "reservation");

        return "views/seller/house/house-seller-reservation";
    }

    @GetMapping("/review")
    public String review(Model model,
                         @RequestParam(name = "page", defaultValue = "1") int page,
                         @RequestParam(name = "pageSize", defaultValue = "10") int pageSize){
        SellerVO seller = (SellerVO) model.getAttribute("seller");

        // 리뷰 통계 조회
        ReviewStatisticsDto statistics = reviewStatisticsService.getReviewStatisticsBySeller(seller.getSellerId());
        
        // 리뷰 목록 조회
        List<HouseReviewVO> reviews = houseReviewService.getReviews(seller.getSellerId(), page, pageSize);
        
        // 미답변 리뷰 수 계산 (실제로는 별도 쿼리 필요)
        long unansweredReviews = reviews.stream()
            .filter(review -> review.getStatus() == 0) // 0: 미답변, 1: 답변완료
            .count();
        
        // 부정 리뷰 수 계산 (3점 이하)
        long negativeReviews = reviews.stream()
            .filter(review -> review.getRating() <= 3.0)
            .count();

        // 모델에 데이터 추가
        model.addAttribute("seller", seller);
        model.addAttribute("currentMenu", "review");
        model.addAttribute("statistics", statistics);
        model.addAttribute("reviews", reviews);
        model.addAttribute("unansweredReviews", unansweredReviews);
        model.addAttribute("negativeReviews", negativeReviews);
        
        log.info("판매자 리뷰 페이지 - 판매자ID: {}, 평균평점: {}, 전체리뷰: {}, 미답변: {}, 부정리뷰: {}", 
                seller.getSellerId(), statistics.getAverageRating(), statistics.getTotalCount(), 
                unansweredReviews, negativeReviews);
        
        return "views/seller/house/house-seller-review";
    }

    // 일단 주석 (나중에 처리)
//    @GetMapping("/promotion")
//    public String promotion(Model model){
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
//        SellerVO seller = userDetails.getSeller();
//
//        // 모델에 데이터 추가
//        model.addAttribute("seller", seller);
//        model.addAttribute("currentMenu", "promotion");
//
//        return "views/seller/house/house-seller-promotion";
//    }
//    @GetMapping("/facility")
//    public String facility(Model model){
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
//        SellerVO seller = userDetails.getSeller();
//
//        // 모델에 데이터 추가
//        model.addAttribute("seller", seller);
//        model.addAttribute("currentMenu", "facility");
//
//        return "views/seller/house/house-seller-facility";
//    }

    @GetMapping("/sales")
    public String sales(Model model){
        model.addAttribute("currentMenu", "sales");
        return "views/seller/house/house-seller-sales";
    }

    @GetMapping("/settings")
    public String settings(Model model){
        model.addAttribute("currentMenu", "settings");
        
        return "views/seller/house/house-seller-settings";
    }

    // 판매자 정보 업데이트
    @PostMapping("/update")
    public String updateSellerInfo(Model model ,SellerVO sellerVO, RedirectAttributes redirectAttributes) {
        try {
            SellerVO currentSeller = (SellerVO) model.getAttribute("seller");
            sellerVO.setSellerId(currentSeller.getSellerId());

            boolean success = sellerService.updateSellerInfo(sellerVO);

            if (success) {
                redirectAttributes.addFlashAttribute("message", "판매자 정보가 성공적으로 업데이트되었습니다.");
                log.info("판매자 정보 업데이트 성공: sellerId = {}", currentSeller.getSellerId());
            } else {
                redirectAttributes.addFlashAttribute("error", "판매자 정보 업데이트에 실패했습니다.");
                log.error("판매자 정보 업데이트 실패: sellerId = {}", currentSeller.getSellerId());
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "판매자 정보 업데이트 중 오류가 발생했습니다.");
            log.error("판매자 정보 업데이트 중 예외 발생", e);
        }

        return "redirect:/seller/house/settings";
    }

    // 비밀번호 변경
    @PostMapping("/change-password")
    public String changePassword(@RequestParam(name = "currentPassword") String currentPassword,
                                @RequestParam(name = "newPassword") String newPassword,
                                @RequestParam(name = "confirmPassword") String confirmPassword,
                                RedirectAttributes redirectAttributes,
                                 Model model) {
        try {
            SellerVO seller = (SellerVO) model.getAttribute("seller");

            // 새 비밀번호 확인
            if (!newPassword.equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("error", "새 비밀번호와 확인 비밀번호가 일치하지 않습니다.");
                return "redirect:/seller/house/settings";
            }

            // 비밀번호 길이 검증
            if (newPassword.length() < 6) {
                redirectAttributes.addFlashAttribute("error", "새 비밀번호는 최소 6자 이상이어야 합니다.");
                return "redirect:/seller/house/settings";
            }

            boolean success = sellerService.changePassword(seller.getSellerId(), currentPassword, newPassword);

            if (success) {
                redirectAttributes.addFlashAttribute("message", "비밀번호가 성공적으로 변경되었습니다.");
                log.info("비밀번호 변경 성공: sellerId = {}", seller.getSellerId());
            } else {
                redirectAttributes.addFlashAttribute("error", "현재 비밀번호가 일치하지 않습니다.");
                log.warn("비밀번호 변경 실패 - 현재 비밀번호 불일치: sellerId = {}", seller.getSellerId());
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "비밀번호 변경 중 오류가 발생했습니다.");
            log.error("비밀번호 변경 중 예외 발생", e);
        }

        return "redirect:/seller/house/settings";
    }

    // 결제 설정 업데이트
    @PostMapping("/payment-settings")
    public String updatePaymentSettings(@RequestParam(name = "bank", required = false) String bank,
                                       @RequestParam(name = "accountNumber", required = false) String accountNumber,
                                       @RequestParam(name = "accountHolder", required = false) String accountHolder,
                                       @RequestParam(name = "creditCard", required = false) String creditCard,
                                       @RequestParam(name = "bankTransfer", required = false) String bankTransfer,
                                       @RequestParam(name = "kakaoPay", required = false) String kakaoPay,
                                       @RequestParam(name = "naverPay", required = false) String naverPay,
                                       RedirectAttributes redirectAttributes,
                                        Model model) {
        try {
            SellerVO seller = (SellerVO) model.getAttribute("seller");

            // 결제 설정 정보를 JSON 형태로 저장 (실제로는 별도 테이블 사용 권장)
            String paymentSettings = String.format(
                "{\"bank\":\"%s\",\"accountNumber\":\"%s\",\"accountHolder\":\"%s\",\"creditCard\":\"%s\",\"bankTransfer\":\"%s\",\"kakaoPay\":\"%s\",\"naverPay\":\"%s\"}",
                bank != null ? bank : "",
                accountNumber != null ? accountNumber : "",
                accountHolder != null ? accountHolder : "",
                creditCard != null ? "true" : "false",
                bankTransfer != null ? "true" : "false",
                kakaoPay != null ? "true" : "false",
                naverPay != null ? "true" : "false"
            );

            boolean success = sellerService.updatePaymentSettings(seller.getSellerId(), paymentSettings);

            if (success) {
                redirectAttributes.addFlashAttribute("message", "결제 설정이 성공적으로 업데이트되었습니다.");
                log.info("결제 설정 업데이트 성공: sellerId = {}", seller.getSellerId());
            } else {
                redirectAttributes.addFlashAttribute("error", "결제 설정 업데이트에 실패했습니다.");
                log.error("결제 설정 업데이트 실패: sellerId = {}", seller.getSellerId());
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "결제 설정 업데이트 중 오류가 발생했습니다.");
            log.error("결제 설정 업데이트 중 예외 발생", e);
        }

        return "redirect:/seller/house/settings";
    }

}
