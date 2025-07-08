package kr.spring.reservation.house.service;

import kr.spring.reservation.house.vo.HouseReservationVO;

import java.util.List;

public interface HouseReservationService {
    public List<HouseReservationVO> getRecentlyReservations(Long sellerId);
    public List<HouseReservationVO> getReservations(Long sellerId, int startRow, int endRow);
}
