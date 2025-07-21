package kr.spring.car.service.impl;

import kr.spring.car.dao.CarMapper;
import kr.spring.car.dao.CarLocationCodeMapper;
import kr.spring.car.service.CarService;
import kr.spring.car.vo.CarVO;
import kr.spring.car.vo.CarLocationCodeVO;
import kr.spring.car.vo.CarReservationVO;
import kr.spring.car.dto.CarSearchDTO;
import kr.spring.car.dto.CarReservationDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {
    private final CarMapper carMapper;
    private final CarLocationCodeMapper locationCodeMapper;

    @Override
    public void registerCar(CarVO carVO) {
        carMapper.insertCar(carVO);
    }

    @Override
    public void updateCar(CarVO carVO) {
        carMapper.updateCar(carVO);
    }

    @Override
    public void deleteCar(Long carId) {
        carMapper.deleteCar(carId);
    }

    @Override
    public CarVO getCar(Long carId) {
        return carMapper.selectCar(carId);
    }

    @Override
    public List<CarVO> getCarListBySeller(Long sellerId) {
        return carMapper.selectCarListBySeller(sellerId);
    }

    @Override
    public List<CarVO> getAllCars() {
        return carMapper.selectAllCars();
    }

    @Override
    public List<CarVO> searchAvailableCars(CarSearchDTO searchDTO) {
        log.info("차량 검색 시작: pickupLocationCode={}, pickupDate={}, returnDate={}", 
                searchDTO.getPickupLocationCode(), searchDTO.getPickupDate(), searchDTO.getReturnDate());
        
        List<CarVO> cars;
        
        // 날짜 정보가 있는 경우 예약 가능한 차량만 검색
        if (searchDTO.getPickupDate() != null && searchDTO.getReturnDate() != null) {
            cars = carMapper.selectAvailableCarsByDate(
                searchDTO.getPickupLocationCode(),
                searchDTO.getPickupDate(),
                searchDTO.getReturnDate()
            );
            log.info("날짜 기반 예약 가능 차량 검색: {}건", cars.size());
        } else {
            // 날짜 정보가 없는 경우 기존 방식으로 검색
            cars = carMapper.selectCarsByPickupAndReturn(
                searchDTO.getPickupLocationCode()
            );
            log.info("기본 차량 검색: {}건", cars.size());
        }
        
        // 검색 조건에 따른 필터링
        List<CarVO> filteredCars = cars.stream()
            .filter(car -> {
                // 차량 타입 필터
                if (searchDTO.getCarType() != null && !searchDTO.getCarType().isEmpty()) {
                    if (!car.getCarType().equals(searchDTO.getCarType())) {
                        return false;
                    }
                }
                
                // 최소 좌석 수 필터
                if (searchDTO.getMinSeats() != null) {
                    if (car.getCarSeats() < searchDTO.getMinSeats()) {
                        return false;
                    }
                }
                
                // 최대 가격 필터
                if (searchDTO.getMaxPrice() != null) {
                    if (car.getCarPrice() > searchDTO.getMaxPrice()) {
                        return false;
                    }
                }
                
                // 연료 타입 필터
                if (searchDTO.getFuelType() != null && !searchDTO.getFuelType().isEmpty() && !searchDTO.getFuelType().equals("전체")) {
                    if (!car.getCarFuel().equals(searchDTO.getFuelType())) {
                        return false;
                    }
                }
                
                return true;
            })
            .toList();
        
        log.info("차량 검색 완료: {}건", filteredCars.size());
        return filteredCars;
    }

    @Override
    public CarReservationVO createReservation(CarReservationDTO reservationDTO) {
        // 예약 중복 체크
        if (checkReservationConflict(reservationDTO.getCarId(), 
                                   reservationDTO.getPickupDate(), 
                                   reservationDTO.getReturnDate())) {
            throw new RuntimeException("해당 기간에 이미 예약이 존재합니다.");
        }
        
        // 예약 정보를 VO로 변환
        CarReservationVO reservation = new CarReservationVO();
        reservation.setCarId(reservationDTO.getCarId());
        reservation.setMemberId(reservationDTO.getMemberId());
        reservation.setStartDate(reservationDTO.getPickupDate().atStartOfDay());
        reservation.setEndDate(reservationDTO.getReturnDate().atStartOfDay());
        reservation.setStatus("RESERVED");
        
        // 예약 생성
        carMapper.insertReservation(reservation);
        
        return reservation;
    }

    @Override
    public boolean checkReservationConflict(Long carId, LocalDate pickupDate, LocalDate returnDate) {
        int conflictCount = carMapper.checkCarReservationConflict(carId, pickupDate, returnDate);
        return conflictCount > 0;
    }

    @Override
    public List<CarReservationVO> getReservationsByMember(Long memberId) {
        return carMapper.selectReservationsByMember(memberId);
    }

    @Override
    public CarReservationVO getReservation(Long reservationId) {
        return carMapper.selectReservation(reservationId);
    }
    
    @Override
    public void updateReservation(CarReservationVO carReservationVO) {
        carMapper.updateReservation(carReservationVO);
    }
    
    @Override
    public void deleteReservation(Long reservationId) {
        carMapper.deleteReservation(reservationId);
    }
    
    @Override
    public void updateCarStatus(Long carId, String status) {
        log.info("차량 상태 업데이트: carId={}, status={}", carId, status);
        Map<String, Object> params = new HashMap<>();
        params.put("carId", carId);
        params.put("status", status);
        carMapper.updateCarStatus(params);
        log.info("차량 상태 업데이트 완료");
    }
} 