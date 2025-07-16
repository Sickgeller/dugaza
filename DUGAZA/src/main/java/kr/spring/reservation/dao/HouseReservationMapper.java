package kr.spring.reservation.dao;

import kr.spring.reservation.vo.HouseReservationVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface HouseReservationMapper {
    public List<HouseReservationVO> findBySellerId(@Param("sellerId") Long sellerId, 
                                                  @Param("startRow") int startRow, 
                                                  @Param("endRow") int endRow);
    void insertReservation(HouseReservationVO houseReservationVO);
    
    /**
     * 예약 중복 체크
     * @param roomId 객실 ID
     * @param reservationStart 예약 시작 시간
     * @param reservationEnd 예약 종료 시간
     * @return 중복 예약 개수
     */
    int checkReservationConflict(@Param("roomId") Long roomId, 
                                @Param("reservationStart") String reservationStart, 
                                @Param("reservationEnd") String reservationEnd);

    List<HouseReservationVO> selectReservationsByMember(Long memberId);
    
    /**
     * 예약 정보 조회
     */
    HouseReservationVO selectReservation(Long reservationId);
    
    /**
     * 예약 정보 업데이트
     */
    void updateReservation(HouseReservationVO houseReservationVO);
    
    /**
     * 예약 삭제
     */
    void deleteReservation(Long reservationId);
}
