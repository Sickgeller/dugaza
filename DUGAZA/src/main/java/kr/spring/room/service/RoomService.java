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

    List<RoomDetailVO> getRoomsWithHouseId(Long contentId);
    
    // 예약 가능한 객실 조회 (날짜별 예약 가능 여부 확인)
    List<RoomDetailVO> getAvailableRooms(Long contentId, String checkInDate, String checkOutDate, int guestCount);
}
