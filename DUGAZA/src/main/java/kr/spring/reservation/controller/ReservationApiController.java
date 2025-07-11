package kr.spring.reservation.controller;

import kr.spring.room.service.RoomService;
import kr.spring.room.vo.RoomDetailVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class ReservationApiController {

    private final RoomService roomService;

    /**
     * 예약 가능한 객실 목록 조회
     */
    @GetMapping("/rooms/available")
    public ResponseEntity<Map<String, Object>> getAvailableRooms(
            @RequestParam("contentId") Long contentId,
            @RequestParam("checkIn") String checkIn,
            @RequestParam("checkOut") String checkOut,
            @RequestParam("guestCount") int guestCount) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<RoomDetailVO> availableRooms = roomService.getAvailableRooms(contentId, checkIn, checkOut, guestCount);
            
            response.put("success", true);
            response.put("rooms", availableRooms);
            response.put("count", availableRooms.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("예약 가능한 객실 조회 실패", e);
            response.put("success", false);
            response.put("message", "객실 정보를 불러오는데 실패했습니다.");
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 객실 상세 정보 조회
     */
    @GetMapping("/rooms/{roomId}")
    public ResponseEntity<Map<String, Object>> getRoomDetail(@PathVariable("roomId") Long roomId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            RoomDetailVO roomDetail = roomService.getRoomById(roomId);
            
            if (roomDetail == null) {
                response.put("success", false);
                response.put("message", "객실 정보를 찾을 수 없습니다.");
                return ResponseEntity.notFound().build();
            }
            
            response.put("success", true);
            response.put("room", roomDetail);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("객실 상세 정보 조회 실패", e);
            response.put("success", false);
            response.put("message", "객실 정보를 불러오는데 실패했습니다.");
            return ResponseEntity.badRequest().body(response);
        }
    }
} 