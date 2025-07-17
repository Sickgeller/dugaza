package kr.spring.car.dao;

import kr.spring.car.vo.CarVO;
import kr.spring.car.vo.CarReservationVO;
import kr.spring.car.dto.CarSearchDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Mapper
public interface CarMapper {
    
    /**
     * 차량 등록
     */
    void insertCar(CarVO carVO);
    
    /**
     * 차량 수정
     */
    void updateCar(CarVO carVO);
    
    /**
     * 차량 삭제
     */
    void deleteCar(@Param("carId") Long carId);
    
    /**
     * 차량 상세 조회
     */
    CarVO selectCar(@Param("carId") Long carId);
    
    /**
     * 판매자별 차량 목록 조회
     */
    List<CarVO> selectCarListBySeller(@Param("sellerId") Long sellerId);
    
    /**
     * 위치별 차량 검색
     */
    List<CarVO> selectCarsByLocation(@Param("locationCode") int locationCode);
    
    /**
     * 수령 위치별 차량 검색 (단순화)
     */
    List<CarVO> selectCarsByPickupAndReturn(@Param("pickupLocationCode") int pickupLocationCode);
    
    /**
     * 예약 가능한 차량 검색 (날짜 기반)
     */
    List<CarVO> selectAvailableCarsByDate(@Param("pickupLocationCode") int pickupLocationCode, 
                                         @Param("pickupDate") LocalDate pickupDate, 
                                         @Param("returnDate") LocalDate returnDate);
    
    // 예약 관련 메서드
    List<CarVO> searchAvailableCars(CarSearchDTO searchDTO);
    void insertReservation(CarReservationVO reservation);
    List<CarReservationVO> selectReservationsByMember(Long memberId);
    CarReservationVO selectReservation(Long reservationId);
    void updateReservation(CarReservationVO reservation);
    void deleteReservation(@Param("reservationId") Long reservationId);
    
    /**
     * 차량 예약 중복 체크
     * @param carId 차량 ID
     * @param pickupDate 수령일
     * @param returnDate 반납일
     * @return 중복 예약 개수
     */
    int checkCarReservationConflict(@Param("carId") Long carId, 
                                   @Param("pickupDate") LocalDate pickupDate, 
                                   @Param("returnDate") LocalDate returnDate);
} 