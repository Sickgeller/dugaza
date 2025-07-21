package kr.spring.seller.service;

import kr.spring.seller.vo.SellerDetailVO;
import kr.spring.seller.vo.SellerVO;
import kr.spring.seller.vo.HouseSellerDetailVO;

public interface SellerService {

    void register(SellerVO sellerVO);
    SellerDetailVO getSellerDetail(SellerVO seller);
    
    // 판매자 정보 업데이트
    boolean updateSellerInfo(SellerVO sellerVO);
    
    // 비밀번호 변경
    boolean changePassword(Long sellerId, String currentPassword, String newPassword);
    
    // 결제 설정 업데이트 (향후 확장 가능)
    boolean updatePaymentSettings(Long sellerId, String paymentSettings);
    
    // houseId로 seller 조회
    HouseSellerDetailVO getSellerByHouseId(Long houseId);
    
    // 숙소-판매자 연결
    void connectHouseToSeller(Long contentId, Long sellerId);
}
