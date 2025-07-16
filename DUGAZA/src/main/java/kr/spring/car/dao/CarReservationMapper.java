package kr.spring.car.dao;

import kr.spring.car.vo.CarReservationVO;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface CarReservationMapper {
    void insertReservation(CarReservationVO reservationVO);
    void updateReservation(CarReservationVO reservationVO);
    void deleteReservation(Long reservationId);
    CarReservationVO selectReservation(Long reservationId);
    List<CarReservationVO> selectReservationsByCar(Long carId);
    List<CarReservationVO> selectReservationsBySeller(Long sellerId);
    List<CarReservationVO> selectReservationsByMember(Long memberId);
}