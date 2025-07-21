package kr.spring.admin.dao;

import kr.spring.seller.vo.SellerVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface AdminMapper {
    
    /**
     * 전체 판매자 목록 조회
     */
    List<SellerVO> selectAllSellers();
    
    /**
     * 판매자 타입별 목록 조회
     */
    List<SellerVO> selectSellersByType(@Param("sellerType") String sellerType);
    
    /**
     * 판매자 상태별 목록 조회
     */
    List<SellerVO> selectSellersByStatus(@Param("status") String status);
    
    /**
     * 판매자 검색
     */
    List<SellerVO> searchSellers(@Param("keyword") String keyword, @Param("searchType") String searchType);
    
    /**
     * 검색 및 필터 조건으로 판매자 목록 조회
     */
    List<SellerVO> selectSellersWithFilter(Map<String, Object> params);
    
    /**
     * 판매자 상태 업데이트
     */
    void updateSellerStatus(@Param("sellerId") Long sellerId, @Param("status") String status);
    
    /**
     * 판매자 통계 조회
     */
    Map<String, Object> getSellerStatistics();
    
    /**
     * 판매자 타입별 통계 조회
     */
    Map<String, Object> getSellerTypeStatistics();
} 