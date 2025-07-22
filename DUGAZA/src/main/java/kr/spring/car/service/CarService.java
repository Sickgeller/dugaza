package kr.spring.car.service;

import kr.spring.car.vo.CarVO;
import kr.spring.car.vo.CarReservationVO;
import kr.spring.car.dto.CarSearchDTO;
import kr.spring.car.dto.CarReservationDTO;

import java.time.LocalDate;
import java.util.List;

public interface CarService {
    
    // 차량 관련 메서드
    void registerCar(CarVO carVO);
    void updateCar(CarVO carVO);
    void deleteCar(Long carId);
    CarVO getCar(Long carId);
    List<CarVO> getCarListBySeller(Long sellerId);
    List<CarVO> getAllCars(); // 전체 차량 목록 조회
    List<CarVO> searchAvailableCars(CarSearchDTO searchDTO);
    
    // 예약 관련 메서드
    CarReservationVO createReservation(CarReservationDTO reservationDTO);
    List<CarReservationVO> getReservationsByMember(Long memberId);
    CarReservationVO getReservation(Long reservationId);
    
    /**
     * 차량 예약 중복 체크
     * @param carId 차량 ID
     * @param pickupDate 수령일
     * @param returnDate 반납일
     * @return 중복이 있으면 true, 없으면 false
     */
    boolean checkReservationConflict(Long carId, LocalDate pickupDate, LocalDate returnDate);
    
    /**
     * 예약 정보 업데이트
     */
    void updateReservation(CarReservationVO carReservationVO);
    
    /**
     * 예약 삭제
     */
    void deleteReservation(Long reservationId);
    
    /**
     * 차량 상태 업데이트
     * @param carId 차량 ID
     * @param status 변경할 상태
     */
    void updateCarStatus(Long carId, String status);
} 