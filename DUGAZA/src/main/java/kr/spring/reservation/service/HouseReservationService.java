package kr.spring.reservation.service;

import kr.spring.reservation.vo.HouseReservationVO;

import java.time.LocalDateTime;
import java.util.List;

public interface HouseReservationService {
    public List<HouseReservationVO> getRecentlyReservations(Long sellerId);
    public List<HouseReservationVO> getReservations(Long sellerId, int startRow, int endRow);
    void insertReservation(HouseReservationVO houseReservationVO);
    
    /**
     * 예약 중복 체크
     * @param roomId 객실 ID
     * @param reservationStart 예약 시작 시간
     * @param reservationEnd 예약 종료 시간
     * @return 중복이 있으면 true, 없으면 false
     */
    boolean checkReservationConflict(Long roomId, LocalDateTime reservationStart, LocalDateTime reservationEnd);
}
