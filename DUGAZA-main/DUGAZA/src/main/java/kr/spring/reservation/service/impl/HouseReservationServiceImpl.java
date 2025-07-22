package kr.spring.reservation.service.impl;

import kr.spring.reservation.dao.HouseReservationMapper;
import kr.spring.reservation.service.HouseReservationService;
import kr.spring.reservation.vo.HouseReservationVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class HouseReservationServiceImpl implements HouseReservationService {

    private final HouseReservationMapper houseReservationMapper;

    @Override
    public List<HouseReservationVO> getRecentlyReservations(Long sellerId) {
        List<HouseReservationVO> result = houseReservationMapper.findBySellerId(sellerId, 1, 5);
        if(result.isEmpty()){
            return new ArrayList<>();
        }
        return result;
    }

    @Override
    public List<HouseReservationVO> getReservations(Long sellerId, int startRow, int endRow) {
        List<HouseReservationVO> result = houseReservationMapper.findBySellerId(sellerId, startRow, endRow);
        if(result.isEmpty()){
            return new ArrayList<>();
        }
        return result;
    }

    @Override
    public void insertReservation(HouseReservationVO houseReservationVO) {
        // 예약 중복 체크
        if (checkReservationConflict(houseReservationVO.getRoomId(), 
                                   houseReservationVO.getReservationStart(), 
                                   houseReservationVO.getReservationEnd())) {
            throw new RuntimeException("해당 기간에 이미 예약이 존재합니다.");
        }
        
        houseReservationMapper.insertReservation(houseReservationVO);
    }
    
    @Override
    public boolean checkReservationConflict(Long roomId, LocalDateTime reservationStart, LocalDateTime reservationEnd) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        String startStr = reservationStart.format(formatter);
        String endStr = reservationEnd.format(formatter);
        
        int conflictCount = houseReservationMapper.checkReservationConflict(roomId, startStr, endStr);
        return conflictCount > 0;
    }
}
