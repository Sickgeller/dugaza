package kr.spring.car.dao;

import kr.spring.car.vo.CarLocationCodeVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CarLocationCodeMapper {
    
    /**
     * 모든 활성화된 위치 코드 조회
     */
    List<CarLocationCodeVO> selectAllActiveLocationCodes();
    
    /**
     * 특정 위치 코드 조회
     */
    CarLocationCodeVO selectLocationCode(@Param("locationCode") int locationCode);
    
    /**
     * 위치명으로 코드 조회
     */
    CarLocationCodeVO selectLocationCodeByName(@Param("locationName") String locationName);
} 