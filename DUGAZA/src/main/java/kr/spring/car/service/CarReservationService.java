package kr.spring.car.service;

import kr.spring.car.vo.CarReservationVO;
import java.util.List;

public interface CarReservationService {
    void registerReservation(CarReservationVO reservationVO);
    void updateReservation(CarReservationVO reservationVO);
    void deleteReservation(Long reservationId);
    CarReservationVO getReservation(Long reservationId);
    List<CarReservationVO> getReservationsByCar(Long carId);
    List<CarReservationVO> getReservationsBySeller(Long sellerId);
} 