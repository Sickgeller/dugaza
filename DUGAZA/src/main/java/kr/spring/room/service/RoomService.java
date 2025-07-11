package kr.spring.room.service;

import kr.spring.room.vo.RoomDetailVO;

import java.util.List;

public interface RoomService {
    List<RoomDetailVO> getRoomsWithSeller(Long id, int startRow, int endRow);
    
    int getTotalRoomCount(Long sellerId);

    void insertRoom(RoomDetailVO roomDetailVO);
    
    void updateRoom(RoomDetailVO roomDetailVO);
    
    void deleteRoom(Long roomId);
    
    RoomDetailVO getRoomById(Long roomId);
}
