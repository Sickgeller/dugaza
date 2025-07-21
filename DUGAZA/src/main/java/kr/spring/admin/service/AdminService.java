package kr.spring.admin.service;

import java.util.Map;
import java.util.List;

public interface AdminService {
    
    /**
     * 대시보드 통계 데이터 조회
     */
    Map<String, Object> getDashboardStats();
    
    /**
     * 회원 상태 수정
     */
    void updateMemberStatus(Long memberId, String status);
    
    /**
     * 회원 상세보기
     */
    Map<String, Object> getMemberDetail(Long memberId);
    
    /**
     * 판매자 목록 조회
     */
    List<Map<String, Object>> getSellerList(Map<String, Object> params);
    
    /**
     * 판매자 상태 수정
     */
    void updateSellerStatus(Long sellerId, String status);
    
    /**
     * 차량 목록 조회
     */
    List<Map<String, Object>> getCarList();
    
    /**
     * 승인 대기 차량 목록 조회
     */
    List<Map<String, Object>> getPendingCarList();
    
    /**
     * 숙소 목록 조회
     */
    List<Map<String, Object>> getHouseList(Map<String, Object> params);
    
    /**
     * 숙소 개수 조회
     */
    int getHouseCount(Map<String, Object> params);
    
    /**
     * 숙소 상세보기
     */
    Map<String, Object> getHouseDetail(Long houseId);
    
    /**
     * 리뷰 목록 조회
     */
    List<Map<String, Object>> getReviewList();
    
    /**
     * 상품 목록 조회 (차량, 숙소, 투어)
     */
    List<Map<String, Object>> getProductList(String productType);
    
    /**
     * 상품 상태 수정
     */
    void updateProductStatus(Long productId, String productType, String status);
    
    /**
     * 리뷰 상태 수정
     */
    void updateReviewStatus(Long reviewId, String status);
    
    /**
     * 문의 목록 조회
     */
    List<Map<String, Object>> getInquiryList();
    
    /**
     * 문의 상태 수정
     */
    void updateInquiryStatus(Long inquiryId, String status);
} 