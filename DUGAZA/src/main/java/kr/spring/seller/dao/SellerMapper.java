package kr.spring.seller.dao;

import kr.spring.seller.vo.SellerVO;
import kr.spring.seller.vo.HouseSellerDetailVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SellerMapper {
    void insertSeller(SellerVO sellerVO);
    SellerVO selectSeller(String id);
    
    // 판매자 정보 업데이트
    void updateSeller(SellerVO sellerVO);
    
    // 비밀번호 변경
    void updatePassword(@Param("sellerId") Long sellerId, @Param("newPassword") String newPassword);
    
    // 현재 비밀번호 확인
    String getCurrentPassword(@Param("sellerId") Long sellerId);
    
    // houseId로 seller 조회
    HouseSellerDetailVO getSellerByHouseId(@Param("houseId") Long houseId);
}
