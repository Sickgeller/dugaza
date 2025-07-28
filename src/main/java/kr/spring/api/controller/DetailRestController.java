package kr.spring.api.controller;

import kr.spring.review.base.service.BaseReviewService;
import kr.spring.review.base.vo.BaseReviewVO;
import kr.spring.room.service.RoomService;
import kr.spring.room.vo.RoomDetailVO;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.List;

@RestController
@RequestMapping("/api/detail")
public class DetailRestController {
    private final BaseReviewService baseReviewService;
    private final RoomService roomService;

    public DetailRestController(BaseReviewService baseReviewService, RoomService roomService) {
        this.baseReviewService = baseReviewService;
        this.roomService = roomService;
    }

    @GetMapping("/{contentType}/{contentId}/reviews")
    public ResponseEntity<List<BaseReviewVO>> getReviews(
        @PathVariable String contentType,
        @PathVariable Long contentId,
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "10") int pageSize
    ) {
        List<BaseReviewVO> reviews = baseReviewService.getReviewsByContentType(contentType, contentId, page, pageSize);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/{contentType}/{contentId}/rooms")
    public ResponseEntity<List<RoomDetailVO>> getRooms(
        @PathVariable String contentType,
        @PathVariable Long contentId,
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "10") int pageSize
    ) {
        List<RoomDetailVO> rooms = roomService.getRoomsByContentType(contentType, contentId, page, pageSize);
        return ResponseEntity.ok(rooms);
    }
} 