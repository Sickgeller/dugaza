//package kr.spring.seller.service;
//
//import kr.spring.seller.dao.SellerMapper;
//import kr.spring.seller.vo.SellerDetailVO;
//import kr.spring.seller.vo.SellerVO;
//import kr.spring.seller.vo.HouseSellerDetailVO;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//
//@Service
//@Slf4j
//@RequiredArgsConstructor
//public class SellerServiceImpl implements SellerService {
//
//    private final SellerMapper sellerMapper;
//    private final PasswordEncoder passwordEncoder;
//
//    @Override
//    public void register(SellerVO sellerVO) {
//        sellerMapper.insertSeller(sellerVO);
//    }
//
//    @Override
//    public SellerDetailVO getSellerDetail(SellerVO seller) {
//        return null;
//    }
//
//    @Override
//    public boolean updateSellerInfo(SellerVO sellerVO) {
//        try {
//            log.info("판매자 정보 업데이트: sellerId = {}", sellerVO.getSellerId());
//            sellerMapper.updateSeller(sellerVO);
//            return true;
//        } catch (Exception e) {
//            log.error("판매자 정보 업데이트 실패: sellerId = {}", sellerVO.getSellerId(), e);
//            return false;
//        }
//    }
//
//    @Override
//    public boolean changePassword(Long sellerId, String currentPassword, String newPassword) {
//        try {
//            log.info("비밀번호 변경 시도: sellerId = {}", sellerId);
//
//            // 현재 비밀번호 확인
//            String storedPassword = sellerMapper.getCurrentPassword(sellerId);
//            if (storedPassword == null || !passwordEncoder.matches(currentPassword, storedPassword)) {
//                log.warn("현재 비밀번호가 일치하지 않음: sellerId = {}", sellerId);
//                return false;
//            }
//
//            // 새 비밀번호 암호화 및 업데이트
//            String encodedNewPassword = passwordEncoder.encode(newPassword);
//            sellerMapper.updatePassword(sellerId, encodedNewPassword);
//
//            log.info("비밀번호 변경 성공: sellerId = {}", sellerId);
//            return true;
//        } catch (Exception e) {
//            log.error("비밀번호 변경 실패: sellerId = {}", sellerId, e);
//            return false;
//        }
//    }
//
//    @Override
//    public boolean updatePaymentSettings(Long sellerId, String paymentSettings) {
//        try {
//            log.info("결제 설정 업데이트: sellerId = {}", sellerId);
//            // TODO: 결제 설정 테이블이 있다면 여기에 구현
//            // 현재는 성공으로 처리
//            return true;
//        } catch (Exception e) {
//            log.error("결제 설정 업데이트 실패: sellerId = {}", sellerId, e);
//            return false;
//        }
//    }
//
//    @Override
//    public HouseSellerDetailVO getSellerByHouseId(Long houseId) {
//        try {
//            log.info("houseId로 seller 조회: houseId = {}", houseId);
//            return sellerMapper.getSellerByHouseId(houseId);
//        } catch (Exception e) {
//            log.error("houseId로 seller 조회 실패: houseId = {}", houseId, e);
//            return null;
//        }
//    }
//
//    @Override
//    public void connectHouseToSeller(Long contentId, Long sellerId) {
//        try {
//            log.info("숙소-판매자 연결: contentId = {}, sellerId = {}", contentId, sellerId);
//            sellerMapper.connectHouseToSeller(contentId, sellerId);
//            log.info("숙소-판매자 연결 성공");
//        } catch (Exception e) {
//            log.error("숙소-판매자 연결 실패: contentId = {}, sellerId = {}", contentId, sellerId, e);
//            throw new RuntimeException("숙소-판매자 연결에 실패했습니다.", e);
//        }
//    }
//}
