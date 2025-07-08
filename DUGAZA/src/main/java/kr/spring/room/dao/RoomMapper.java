package kr.spring.room.dao;

import kr.spring.room.dto.RoomDetailVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface RoomMapper {
    /**
     * @param sellerId 판매중인 셀러의 id
     * @param startRow 시작 행 번호
     * @param endRow 끝 행 번호
     * @return 셀러가 관리(판매)중인 room의 List (페이징 처리됨)
     */
    List<RoomDetailVO> getRoomsWithSellerId(
        @Param("sellerId") Long sellerId,
        @Param("startRow") int startRow,
        @Param("endRow") int endRow
    );

    /**
     * @param sellerId 판매중인 셀러의 id
     * @return 셀러가 관리(판매)중인 총 객실 수
     */
    int getTotalRoomCount(@Param("sellerId") Long sellerId);
}
