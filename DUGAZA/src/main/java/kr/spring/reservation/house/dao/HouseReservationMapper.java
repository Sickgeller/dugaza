package kr.spring.reservation.house.dao;

import kr.spring.reservation.house.vo.HouseReservationVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface HouseReservationMapper {
    public List<HouseReservationVO> findBySellerId(@Param("sellerId") Long sellerId, 
                                                  @Param("startRow") int startRow, 
                                                  @Param("endRow") int endRow);
}
