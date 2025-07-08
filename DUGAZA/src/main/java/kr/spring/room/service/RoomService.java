package kr.spring.room.service;

import kr.spring.room.dto.RoomDetailVO;

import java.util.List;

public interface RoomService {
    List<RoomDetailVO> getRoomsWithSeller(Long id, int startRow, int endRow);
    
    int getTotalRoomCount(Long sellerId);
}
