package kr.spring.review.house.dao;

import kr.spring.review.house.vo.HouseReviewVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface HouseReviewMapper {
    public List<HouseReviewVO> findHouseReviewBySellerId(@Param("sellerId") Long sellerId, 
                                                        @Param("startRow") int startRow, 
                                                        @Param("endRow") int endRow);
}
