package kr.spring.car.service.impl;

import kr.spring.car.dao.CarReservationMapper;
import kr.spring.car.service.CarReservationService;
import kr.spring.car.vo.CarReservationVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CarReservationServiceImpl implements CarReservationService {
    private final CarReservationMapper carReservationMapper;

    @Override
    public void registerReservation(CarReservationVO reservationVO) {
        carReservationMapper.insertReservation(reservationVO);
    }

    @Override
    public void updateReservation(CarReservationVO reservationVO) {
        carReservationMapper.updateReservation(reservationVO);
    }

    @Override
    public void deleteReservation(Long reservationId) {
        carReservationMapper.deleteReservation(reservationId);
    }

    @Override
    public CarReservationVO getReservation(Long reservationId) {
        return carReservationMapper.selectReservation(reservationId);
    }

    @Override
    public List<CarReservationVO> getReservationsByCar(Long carId) {
        return carReservationMapper.selectReservationsByCar(carId);
    }

    @Override
    public List<CarReservationVO> getReservationsBySeller(Long sellerId) {
        return carReservationMapper.selectReservationsBySeller(sellerId);
    }

    @Override
    public List<CarReservationVO> getReservationsByMember(Long memberId) {
        return carReservationMapper.selectReservationsByMember(memberId);
    }
} 