package kr.spring.reservation.controller;

import kr.spring.house.service.HouseService;
import kr.spring.house.vo.HouseVO;
import kr.spring.member.vo.MemberVO;
import kr.spring.reservation.service.HouseReservationService;
import kr.spring.reservation.vo.HouseReservationVO;
import kr.spring.room.service.RoomService;
import kr.spring.room.vo.RoomDetailVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/reservation")
@RequiredArgsConstructor
@Slf4j
public class ReservationController {

    private final HouseService houseService;
    private final RoomService roomService;
    private final HouseReservationService houseReservationService;

    /**
     * 숙소 예약 페이지
     */
    @GetMapping("/house")
    public String reservation(@RequestParam("contentId") Long contentId, Model model) {
        try {
            // 숙소 정보 조회
            HouseVO houseInfo = houseService.selectHouse(contentId);
            
            if (houseInfo == null) {
                return "redirect:/house/list?error=not_found";
            }
            
            // 해당 숙소의 모든 객실 목록 조회
            List<RoomDetailVO> roomList = roomService.getRoomsWithHouseId(contentId);
            
            model.addAttribute("houseInfo", houseInfo);
            model.addAttribute("contentId", contentId);
            model.addAttribute("roomList", roomList);
            
            return "views/house/house-reservation";
        } catch (Exception e) {
            log.error("예약 페이지 로드 실패", e);
            return "redirect:/house/list?error=load_failed";
        }
    }

    @PostMapping("/house/book")
    @ResponseBody
    public Map<String, Object> reservationBook(@ModelAttribute HouseReservationVO houseReservationVO, Model model) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 회원 정보 가져오기
            MemberVO member = (MemberVO) model.getAttribute("member");
            if (member == null) {
                response.put("success", false);
                response.put("message", "로그인이 필요합니다.");
                return response;
            }
            
            // 필수 데이터 검증
            if (houseReservationVO.getHouseId() == null || houseReservationVO.getRoomId() == null) {
                response.put("success", false);
                response.put("message", "필수 정보가 누락되었습니다.");
                return response;
            }
            
            // 회원 ID 설정
            houseReservationVO.setMemberId(member.getMemberId());
            
            // 상태 설정 (0: 예약 대기)
            houseReservationVO.setStatus(0);
            
            // 예약 처리
            houseReservationService.insertReservation(houseReservationVO);
            
            response.put("success", true);
            response.put("message", "예약이 성공적으로 완료되었습니다.");
            
        } catch (RuntimeException e) {
            // 예약 중복 등 예약 관련 오류
            log.error("예약 처리 중 오류 발생: {}", e.getMessage());
            response.put("success", false);
            response.put("message", e.getMessage());
        } catch (Exception e) {
            log.error("예약 처리 중 오류 발생", e);
            response.put("success", false);
            response.put("message", "예약 처리 중 오류가 발생했습니다.");
        }
        
        return response;
    }
} 