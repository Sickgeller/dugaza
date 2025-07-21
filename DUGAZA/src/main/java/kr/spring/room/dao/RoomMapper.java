package kr.spring.room.dao;

import kr.spring.room.vo.RoomDetailVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface RoomMapper {
    /**
     * @param sellerId 판매중인 셀러의 id
     * @param startRow 시작 행 번호
     * @param endRow 끝 행 번호
     * @return 셀러가 관리(판매)중인 room의 List (페이징 처리됨)
     */
    List<RoomDetailVO> getRoomsWithSellerId(
        @Param("sellerId") Long sellerId,
        @Param("startRow") int startRow,
        @Param("endRow") int endRow
    );

    /**
     * @param sellerId 판매중인 셀러의 id
     * @return 셀러가 관리(판매)중인 총 객실 수
     */
    int getTotalRoomCount(@Param("sellerId") Long sellerId);

    /**
     * @param roomDetailVO 등록할 방 정보
     */
    void insertRoom(RoomDetailVO roomDetailVO);
    
    /**
     * @param roomDetailVO 수정할 방 정보
     */
    void updateRoom(RoomDetailVO roomDetailVO);
    
    /**
     * @param roomId 삭제할 방의 ID
     */
    void deleteRoom(@Param("roomId") Long roomId);
    
    /**
     * @param roomId 조회할 방의 ID
     * @return 방 상세 정보
     */
    RoomDetailVO getRoomById(@Param("roomId") Long roomId);

    /**
     * @param contentId
     */
    List<RoomDetailVO> getRoomByHouseId(Long contentId);
    
    /**
     * @param contentId 숙소 ID
     * @param checkInDate 체크인 날짜
     * @param checkOutDate 체크아웃 날짜
     * @param guestCount 투숙 인원
     * @return 예약 가능한 객실 목록
     */
    List<RoomDetailVO> getAvailableRooms(@Param("contentId") Long contentId,
                                        @Param("checkInDate") String checkInDate,
                                        @Param("checkOutDate") String checkOutDate,
                                        @Param("guestCount") int guestCount);
    
    /**
     * @param houseId 숙소 ID
     * @return 해당 숙소의 객실 목록 (RoomDetailVO 사용)
     */
    List<RoomDetailVO> getRoomsByHouseId(@Param("houseId") Long houseId);

    List<RoomDetailVO> selectRoomsByHouseIdWithPaging(@Param("houseId") Long houseId, @Param("offset") int offset, @Param("limit") int limit);
    int countRoomsByHouseId(@Param("houseId") Long houseId);
}
