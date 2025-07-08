package kr.spring.reservation.house.service;

import kr.spring.reservation.house.dao.HouseReservationMapper;
import kr.spring.reservation.house.vo.HouseReservationVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
}
