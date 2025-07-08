package kr.spring.seller.controller;

import kr.spring.auth.security.CustomUserDetails;
import kr.spring.common.SellerType;
import kr.spring.house.service.HouseService;
import kr.spring.reservation.house.service.HouseReservationService;
import kr.spring.reservation.house.vo.HouseReservationVO;
import kr.spring.review.house.service.HouseReviewService;
import kr.spring.review.house.vo.HouseReviewVO;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

                // 판매자 정보
                model.addAttribute("seller", seller);

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
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        SellerVO seller = userDetails.getSeller();
        
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
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        SellerVO seller = userDetails.getSeller();

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
    public String review(Model model){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        SellerVO seller = userDetails.getSeller();
        
        // 모델에 데이터 추가
        model.addAttribute("seller", seller);
        model.addAttribute("currentMenu", "review");
        
        return null;
    }

    @GetMapping("/promotion")
    public String promotion(Model model){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        SellerVO seller = userDetails.getSeller();
        
        // 모델에 데이터 추가
        model.addAttribute("seller", seller);
        model.addAttribute("currentMenu", "promotion");
        
        return "views/seller/house/house-seller-promotion";
    }

    @GetMapping("/facility")
    public String facility(Model model){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        SellerVO seller = userDetails.getSeller();
        
        // 모델에 데이터 추가
        model.addAttribute("seller", seller);
        model.addAttribute("currentMenu", "facility");
        
        return "views/seller/house/house-seller-facility";
    }

    @GetMapping("/sales")
    public String sales(Model model){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        SellerVO seller = userDetails.getSeller();
        
        // 모델에 데이터 추가
        model.addAttribute("seller", seller);
        model.addAttribute("currentMenu", "sales");
        
        return "views/seller/house/house-seller-sales";
    }

    @GetMapping("/settings")
    public String settings(Model model){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        SellerVO seller = userDetails.getSeller();
        
        // 모델에 데이터 추가
        model.addAttribute("seller", seller);
        model.addAttribute("currentMenu", "settings");
        
        return "views/seller/house/house-seller-settings";
    }
}
