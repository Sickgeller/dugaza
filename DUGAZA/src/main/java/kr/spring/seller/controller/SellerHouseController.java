package kr.spring.seller.controller;

import kr.spring.auth.security.CustomUserDetails;
import kr.spring.common.SellerType;
import kr.spring.house.service.HouseService;
import kr.spring.house.vo.HouseVO;
import kr.spring.reservation.service.HouseReservationService;
import kr.spring.reservation.vo.HouseReservationVO;
import kr.spring.review.base.service.BaseReviewService;
import kr.spring.review.base.vo.BaseReviewVO;
import kr.spring.review.base.vo.ReviewStatisticsVO;
import kr.spring.review.base.service.ReviewStatisticsService;
import kr.spring.room.vo.RoomDetailVO;
import kr.spring.room.service.RoomService;
import kr.spring.seller.service.SellerService;
import kr.spring.seller.vo.SellerVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.ResponseBody;
import kr.spring.seller.vo.HouseSellerDetailVO;
import jakarta.servlet.http.HttpServletRequest;

@RequestMapping("/seller/house")
@Controller
@Slf4j
@RequiredArgsConstructor
public class SellerHouseController {

    private final SellerService sellerService;
    private final HouseService houseService;
    private final RoomService roomService;
    private final HouseReservationService houseReservationService;
    private final BaseReviewService baseReviewService;
    private final ReviewStatisticsService reviewStatisticsService;



    @GetMapping("/")
    public String main(Model model) {
        try {

            if(model.getAttribute("seller") != null && ((SellerVO)model.getAttribute("seller")).getSellerType().equals(SellerType.HOUSE.getValue())) {
                SellerVO seller = (SellerVO)model.getAttribute("seller");
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
                List<BaseReviewVO> recentlyReviews = baseReviewService.getRecentlyReviews(seller.getSellerId());
                model.addAttribute("recentReviews", recentlyReviews);

                // 현재 메뉴 설정
                model.addAttribute("currentMenu", "dashboard");

                log.info("숙소 판매자 대시보드 - 총 객실: {}, 예약된 객실: {}, 예약률: {}%, 최근 예약: {}건",
                        totalRooms, reservatedRooms, reservedRate, reservationList.size());

                return "views/seller/house/house-seller-main";
            } else {
                log.warn("잘못된 판매자 타입: {}", model.getAttribute("seller") != null ? ((SellerVO)model.getAttribute("seller")).getSellerType() : "null");
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
    public String rooms(Model model, 
                       @RequestParam(name = "page", defaultValue = "1") int page,
                       @RequestParam(name = "houseId", required = false) Long houseId,
                       @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        // 세션에서 seller 정보 가져오기
        SellerVO seller = customUserDetails.getSeller();
        if(seller == null) return "redirect:/seller/login";
        model.addAttribute("seller", seller);

        // 판매자가 소유한 숙소 목록 조회
        List<HouseVO> houseList = houseService.selectHousesWithSellerId(seller.getSellerId());
        model.addAttribute("houseList", houseList);

        // 선택된 숙소 ID 결정
        Long selectedHouseId = houseId;
        if (selectedHouseId == null && !houseList.isEmpty()) {
            selectedHouseId = houseList.get(0).getContentId();
        }
        model.addAttribute("selectedHouseId", selectedHouseId);

        // 해당 숙소의 객실 목록 조회
        int pageSize = 10;
        int currentPage = Math.max(1, page);
        List<RoomDetailVO> rooms = List.of();
        int totalRooms = 0;
        int availableRooms = 0;
        if (selectedHouseId != null) {
            rooms = roomService.getRoomsByHouseId(selectedHouseId, currentPage, pageSize);
            totalRooms = roomService.getTotalRoomCountByHouseId(selectedHouseId);
            availableRooms = (int)rooms.stream().filter(room -> room.getStatus() == 0).count();
        }
        model.addAttribute("rooms", rooms);
        model.addAttribute("totalRooms", totalRooms);
        model.addAttribute("availableRooms", availableRooms);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("totalPages", (int)Math.ceil((double)totalRooms / pageSize));
        model.addAttribute("currentMenu", "management");

        return "views/seller/house/house-seller-rooms";
    }

    @GetMapping("/reservation")
    public String reservation(Model model,
                              @RequestParam(name = "page", defaultValue = "1") int page,
                              @RequestParam(name = "pageSize", defaultValue = "10") int pageSize){
        SellerVO seller = (SellerVO)model.getAttribute("seller");
        if(seller == null) return "redirect:/seller/login";
        model.addAttribute("seller", seller);

        // 페이징 처리를 위한 설정
        int startRow = (page - 1) * pageSize + 1;
        int endRow = page * pageSize;

        // 예약 목록 조회
        List<HouseReservationVO> reservations = houseReservationService.getReservations(seller.getSellerId(), startRow, endRow);
        
        // 최근 예약 목록 조회 (메인 대시보드용)
        List<HouseReservationVO> recentReservations = houseReservationService.getRecentlyReservations(seller.getSellerId());

        // 모델에 데이터 추가
        model.addAttribute("reservations", reservations);
        model.addAttribute("currentPage", page);
        model.addAttribute("currentMenu", "reservation");

        return "views/seller/house/house-seller-reservation";
    }

    @GetMapping("/review")
    public String review(Model model,
                         @RequestParam(name = "page", defaultValue = "1") int page,
                         @RequestParam(name = "pageSize", defaultValue = "10") int pageSize){
        SellerVO seller = (SellerVO)model.getAttribute("seller");
        if(seller == null) return "redirect:/seller/login";
        model.addAttribute("seller", seller);
        
        // 리뷰 통계 조회
        ReviewStatisticsVO statistics = reviewStatisticsService.getReviewStatisticsBySeller(seller.getSellerId());
        
        // 리뷰 목록 조회
        List<BaseReviewVO> reviews = baseReviewService.getReviews(seller.getSellerId(), page, pageSize);
        
        // 미답변 리뷰 수 계산 (실제로는 별도 쿼리 필요)
        long unansweredReviews = reviews.stream()
            .filter(review -> review.getStatus() == 0) // 0: 미답변, 1: 답변완료
            .count();
        
        // 부정 리뷰 수 계산 (3점 이하)
        long negativeReviews = reviews.stream()
            .filter(review -> review.getRating() <= 3.0)
            .count();

        // 모델에 데이터 추가
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
    public String updateSellerInfo(Model model, SellerVO sellerVO, RedirectAttributes redirectAttributes) {
        try {
            SellerVO currentSeller = (SellerVO) model.getAttribute("seller");
            
            if(currentSeller == null) {
                return "redirect:/seller/login";
            }
            
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
            
            if(seller == null) {
                return "redirect:/seller/login";
            }

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
            
            if(seller == null) {
                return "redirect:/seller/login";
            }

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

    @GetMapping("/room/add")
    public String addRoom(
            Model model,
            @RequestParam(name = "contentId", required = true) Long contentId
    ) {
        HouseVO house = houseService.selectHouse(contentId);
        model.addAttribute("house", house);
        return "views/seller/house/house-seller-room-add";
    }

    @PostMapping("/room/insert")
    public String insertRoom(
            @ModelAttribute RoomDetailVO roomDetailVO,
            @RequestParam(name = "image1File", required = false) MultipartFile image1File,
            @RequestParam(name = "image2File", required = false) MultipartFile image2File,
            @RequestParam(name = "image3File", required = false) MultipartFile image3File,
            @RequestParam(name = "contentId", required = true) Long contentId,
            Model model,
            RedirectAttributes redirectAttributes,
            jakarta.servlet.http.HttpServletRequest request
    ) {
        try {
            // houseId를 contentId로 명확히 세팅
            roomDetailVO.setHouseId(contentId);
            // 이미지 파일 저장 및 파일명 세팅
            if (image1File != null && !image1File.isEmpty()) {
                String filename = kr.spring.util.FileUtil.createFile(request, image1File);
                roomDetailVO.setImage1(filename);
            }
            if (image2File != null && !image2File.isEmpty()) {
                String filename = kr.spring.util.FileUtil.createFile(request, image2File);
                roomDetailVO.setImage2(filename);
            }
            if (image3File != null && !image3File.isEmpty()) {
                String filename = kr.spring.util.FileUtil.createFile(request, image3File);
                roomDetailVO.setImage3(filename);
            }
            // 상태 기본값(0: 사용가능)
            roomDetailVO.setStatus(0);
            // DB 저장
            roomService.insertRoom(roomDetailVO);
            redirectAttributes.addFlashAttribute("message", "객실이 성공적으로 등록되었습니다.");
        } catch (Exception e) {
            log.error("객실 등록 중 오류 발생", e);
            redirectAttributes.addFlashAttribute("error", "객실 등록 중 오류가 발생했습니다.");
        }
        // 등록 후 객실 목록으로 이동
        return "redirect:/seller/house/management";
    }
    
    // 객실 수정 페이지
    @GetMapping("/room/edit")
    public String editRoom(@RequestParam("roomId") Long roomId, Model model) {
        try {
            RoomDetailVO room = roomService.getRoomById(roomId);
            if (room == null) {
                return "redirect:/seller/house/management";
            }
            model.addAttribute("room", room);
            return "views/seller/house/house-seller-room-edit";
        } catch (Exception e) {
            log.error("객실 수정 페이지 로드 중 오류 발생", e);
            return "redirect:/seller/house/management";
        }
    }
    
    // 객실 수정 처리
    @PostMapping("/room/update")
    public String updateRoom(
            @ModelAttribute RoomDetailVO roomDetailVO,
            @RequestParam(name = "image1File", required = false) MultipartFile image1File,
            @RequestParam(name = "image2File", required = false) MultipartFile image2File,
            @RequestParam(name = "image3File", required = false) MultipartFile image3File,
            RedirectAttributes redirectAttributes,
            jakarta.servlet.http.HttpServletRequest request
    ) {
        try {
            // 체크박스 필드들이 NULL일 때 0으로 설정
            if (roomDetailVO.getWifi() == null) roomDetailVO.setWifi(0);
            if (roomDetailVO.getTv() == null) roomDetailVO.setTv(0);
            if (roomDetailVO.getAircon() == null) roomDetailVO.setAircon(0);
            if (roomDetailVO.getBathroom() == null) roomDetailVO.setBathroom(0);
            if (roomDetailVO.getSofa() == null) roomDetailVO.setSofa(0);
            if (roomDetailVO.getKitchen() == null) roomDetailVO.setKitchen(0);
            if (roomDetailVO.getPet() == null) roomDetailVO.setPet(0);
            if (roomDetailVO.getSmokingRoom() == null) roomDetailVO.setSmokingRoom(0);
            
            // 기존 객실 정보 조회
            RoomDetailVO existingRoom = roomService.getRoomById(roomDetailVO.getRoomId());
            if (existingRoom == null) {
                redirectAttributes.addFlashAttribute("error", "객실을 찾을 수 없습니다.");
                return "redirect:/seller/house/management";
            }
            
            // 이미지 파일이 업로드된 경우에만 새로 저장
            if (image1File != null && !image1File.isEmpty()) {
                String filename = kr.spring.util.FileUtil.createFile(request, image1File);
                roomDetailVO.setImage1(filename);
            } else {
                roomDetailVO.setImage1(existingRoom.getImage1());
            }
            
            if (image2File != null && !image2File.isEmpty()) {
                String filename = kr.spring.util.FileUtil.createFile(request, image2File);
                roomDetailVO.setImage2(filename);
            } else {
                roomDetailVO.setImage2(existingRoom.getImage2());
            }
            
            if (image3File != null && !image3File.isEmpty()) {
                String filename = kr.spring.util.FileUtil.createFile(request, image3File);
                roomDetailVO.setImage3(filename);
            } else {
                roomDetailVO.setImage3(existingRoom.getImage3());
            }
            
            // DB 업데이트
            roomService.updateRoom(roomDetailVO);
            redirectAttributes.addFlashAttribute("message", "객실이 성공적으로 수정되었습니다.");
        } catch (Exception e) {
            log.error("객실 수정 중 오류 발생", e);
            redirectAttributes.addFlashAttribute("error", "객실 수정 중 오류가 발생했습니다.");
        }
        return "redirect:/seller/house/management";
    }
    
    // 객실 삭제
    @PostMapping("/room/delete")
    @ResponseBody
    public Map<String, Object> deleteRoom(@RequestParam("roomId") Long roomId) {
        Map<String, Object> response = new HashMap<>();
        try {
            roomService.deleteRoom(roomId);
            response.put("success", true);
            response.put("message", "객실이 성공적으로 삭제되었습니다.");
        } catch (Exception e) {
            log.error("객실 삭제 중 오류 발생", e);
            response.put("success", false);
            response.put("message", "객실 삭제 중 오류가 발생했습니다.");
        }
        return response;
    }

    @GetMapping("/apply")
    public String applyForm(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "category", required = false) String category,
            @RequestParam(name = "sort", defaultValue = "latest") String sort,
            Model model,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        
        // 현재 로그인한 판매자 정보 가져오기
        SellerVO seller = customUserDetails.getSeller();
        if (seller == null) return "redirect:/seller/login";
        
        // 페이징 처리
        int pageSize = 12;
        Map<String, Object> params = new HashMap<>();
        
        // 현재 판매자 ID 전달
        params.put("sellerId", seller.getSellerId());
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            params.put("keyword", keyword.trim());
        }
        if (category != null && !category.trim().isEmpty()) {
            params.put("cat3", category.trim());
        }
        params.put("sort", sort);
        
        int startRow = (page - 1) * pageSize + 1;
        int endRow = page * pageSize;
        params.put("start", startRow);
        params.put("end", endRow);
        
        // selectList 대신 selectListForApply 사용
        List<HouseVO> houseList = houseService.selectListForApply(params);
        int totalCount = houseService.selectRowCount(params);
        
        model.addAttribute("houseList", houseList);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("totalPages", (int)Math.ceil((double)totalCount / pageSize));
        model.addAttribute("keyword", keyword);
        model.addAttribute("category", category);
        model.addAttribute("sort", sort);
        model.addAttribute("currentMenu", "apply");
        
        return "views/seller/house/apply";
    }

    @PostMapping("/apply")
    public String applySubmit(
            @RequestParam(name = "contentId") Long contentId,
            HttpServletRequest request,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        SellerVO seller = (SellerVO) request.getSession().getAttribute("seller");
        if (seller == null) return "redirect:/seller/login";
        
        try {
            // 숙소 정보 조회
            HouseVO house = houseService.selectHouse(contentId);
            if (house == null) {
                redirectAttributes.addFlashAttribute("error", "선택한 숙소를 찾을 수 없습니다.");
                return "redirect:/seller/house/apply";
            }
            
            // HouseSellerDetailVO 생성 및 저장
            HouseSellerDetailVO houseSellerDetailVO = new HouseSellerDetailVO();
            houseSellerDetailVO.setHouseId(contentId);
            houseSellerDetailVO.setSellerId(seller.getSellerId());
            houseSellerDetailVO.setStatus("suspending");
            
            houseService.applyHouse(houseSellerDetailVO);
            
            redirectAttributes.addFlashAttribute("message", "숙소 추가 신청이 완료되었습니다. 관리자의 승인 후 사용 가능합니다.");
            log.info("숙소 신청 완료: sellerId={}, houseId={}", seller.getSellerId(), contentId);
            
        } catch (Exception e) {
            log.error("숙소 신청 중 오류 발생", e);
            redirectAttributes.addFlashAttribute("error", "숙소 신청 중 오류가 발생했습니다.");
        }
        
        return "redirect:/seller/house/apply";
    }


}
