package kr.spring.payment.service.impl;

import kr.spring.car.vo.CarReservationVO;
import kr.spring.payment.dao.PaymentPendingMapper;
import kr.spring.payment.dao.PaymentPendingItemMapper;
import kr.spring.payment.service.PaymentPendingService;
import kr.spring.payment.vo.PaymentPendingVO;
import kr.spring.payment.vo.PaymentPendingItemVO;
import kr.spring.car.service.CarService;
import kr.spring.house.service.HouseService;
import kr.spring.reservation.service.HouseReservationService;
import kr.spring.reservation.vo.HouseReservationVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PaymentPendingServiceImpl implements PaymentPendingService {
    
    private final PaymentPendingMapper paymentPendingMapper;
    private final PaymentPendingItemMapper paymentPendingItemMapper;
    private final CarService carService;
    private final HouseService houseService;
    private final HouseReservationService houseReservationService;
    
    @Override
    public PaymentPendingVO createPaymentPending(Long memberId, List<Long> reservationIds) {
        try {
            // 1. 총 금액 계산
            int totalPrice = calculateTotalPrice(reservationIds);
            
            // 2. 할인 금액 계산
            int discountAmount = calculateDiscount(reservationIds);
            int finalPrice = totalPrice - discountAmount;
            
            // 3. 결제 대기 생성
            PaymentPendingVO paymentPending = PaymentPendingVO.builder()
                .memberId(memberId)
                .totalPrice(totalPrice)
                .discountAmount(discountAmount)
                .finalPrice(finalPrice)
                .status(PaymentPendingVO.STATUS_PENDING)
                .expiresAt(LocalDateTime.now().plusMinutes(30))
                .build();
            
            paymentPendingMapper.insertPaymentPending(paymentPending);
            
            // 4. 결제 대기 아이템들 생성
            for (Long reservationId : reservationIds) {
                String reservationType = getReservationType(reservationId);
                
                PaymentPendingItemVO item = PaymentPendingItemVO.builder()
                    .paymentPendingId(paymentPending.getPaymentPendingId())
                    .reservationId(reservationId)
                    .reservationType(reservationType)
                    .build();
                
                paymentPendingItemMapper.insertPaymentPendingItem(item);
            }
            
            log.info("결제 대기 생성 완료: paymentPendingId={}, memberId={}, totalPrice={}", 
                paymentPending.getPaymentPendingId(), memberId, totalPrice);
            
            return paymentPending;
            
        } catch (Exception e) {
            log.error("결제 대기 생성 중 오류 발생: memberId={}, reservationIds={}", memberId, reservationIds, e);
            throw new RuntimeException("결제 대기 생성에 실패했습니다.", e);
        }
    }
    
    @Override
    public PaymentPendingVO getPaymentPending(Long paymentPendingId) {
        return paymentPendingMapper.selectPaymentPending(paymentPendingId);
    }
    
    @Override
    public List<PaymentPendingVO> getPaymentPendingByMember(Long memberId) {
        return paymentPendingMapper.selectPaymentPendingByMember(memberId);
    }
    
    @Override
    public List<PaymentPendingItemVO> getPaymentPendingItems(Long paymentPendingId) {
        return paymentPendingItemMapper.selectPaymentPendingItems(paymentPendingId);
    }
    
    @Override
    public void processPayment(Long paymentPendingId) {
        try {
            // 1. 결제 대기 정보 조회
            PaymentPendingVO paymentPending = getPaymentPending(paymentPendingId);
            if (paymentPending == null) {
                throw new RuntimeException("결제 대기 정보를 찾을 수 없습니다.");
            }
            
            // 2. 만료 여부 확인
            if (paymentPending.isExpired(LocalDateTime.now())) {
                throw new RuntimeException("결제 대기 시간이 만료되었습니다.");
            }
            
            // 3. 관련 예약들 조회
            List<PaymentPendingItemVO> items = getPaymentPendingItems(paymentPendingId);
            
            // 4. 예약 상태 업데이트 (확정)
            for (PaymentPendingItemVO item : items) {
                if (PaymentPendingItemVO.TYPE_HOUSE.equals(item.getReservationType())) {
                    // 숙소 예약 상태를 확정(1)으로 업데이트
                    HouseReservationVO houseReservation = houseReservationService.getReservation(item.getReservationId());
                    if (houseReservation != null) {
                        houseReservation.setStatus(1); // 1: 확정
                        houseReservationService.updateReservation(houseReservation);
                    }
                } else if (PaymentPendingItemVO.TYPE_CAR.equals(item.getReservationType())) {
                    // 차량 예약 상태를 확정으로 업데이트

                    CarReservationVO carReservation = carService.getReservation(item.getReservationId());
                    if (carReservation != null) {
                        carReservation.setStatus("CONFIRMED");
                        carService.updateReservation(carReservation);
                    }
                }
            }
            
            // 5. 결제 대기 상태 업데이트
            paymentPendingMapper.updateStatus(paymentPendingId, PaymentPendingVO.STATUS_PAID);
            
            log.info("결제 처리 완료: paymentPendingId={}", paymentPendingId);
            
        } catch (Exception e) {
            log.error("결제 처리 중 오류 발생: paymentPendingId={}", paymentPendingId, e);
            throw new RuntimeException("결제 처리에 실패했습니다.", e);
        }
    }
    
    @Override
    public void cancelPaymentPending(Long paymentPendingId) {
        try {
            // 1. 결제 대기 정보 조회
            PaymentPendingVO paymentPending = getPaymentPending(paymentPendingId);
            if (paymentPending == null) {
                throw new RuntimeException("결제 대기 정보를 찾을 수 없습니다.");
            }
            
            // 2. 관련 예약들 조회
            List<PaymentPendingItemVO> items = getPaymentPendingItems(paymentPendingId);
            
            // 3. 예약 취소
            for (PaymentPendingItemVO item : items) {
                if (PaymentPendingItemVO.TYPE_HOUSE.equals(item.getReservationType())) {
                    // 숙소 예약 삭제
                    houseReservationService.deleteReservation(item.getReservationId());
                } else if (PaymentPendingItemVO.TYPE_CAR.equals(item.getReservationType())) {
                    // 차량 예약 삭제
                    carService.deleteReservation(item.getReservationId());
                }
            }
            
            // 4. 결제 대기 상태 업데이트
            paymentPendingMapper.updateStatus(paymentPendingId, PaymentPendingVO.STATUS_CANCELLED);
            
            log.info("결제 대기 취소 완료: paymentPendingId={}", paymentPendingId);
            
        } catch (Exception e) {
            log.error("결제 대기 취소 중 오류 발생: paymentPendingId={}", paymentPendingId, e);
            throw new RuntimeException("결제 대기 취소에 실패했습니다.", e);
        }
    }
    
    @Override
    public void expirePaymentPending(Long paymentPendingId) {
        try {
            // 1. 관련 예약들 조회
            List<PaymentPendingItemVO> items = getPaymentPendingItems(paymentPendingId);
            
            // 2. 예약 취소 (재고 해제)
            for (PaymentPendingItemVO item : items) {
                if (PaymentPendingItemVO.TYPE_HOUSE.equals(item.getReservationType())) {
                    // 숙소 예약 삭제
                    houseReservationService.deleteReservation(item.getReservationId());
                } else if (PaymentPendingItemVO.TYPE_CAR.equals(item.getReservationType())) {
                    // 차량 예약 삭제
                    carService.deleteReservation(item.getReservationId());
                }
            }
            
            // 3. 결제 대기 상태 업데이트
            paymentPendingMapper.updateStatus(paymentPendingId, PaymentPendingVO.STATUS_EXPIRED);
            
            log.info("결제 대기 만료 처리 완료: paymentPendingId={}", paymentPendingId);
            
        } catch (Exception e) {
            log.error("결제 대기 만료 처리 중 오류 발생: paymentPendingId={}", paymentPendingId, e);
            throw new RuntimeException("결제 대기 만료 처리에 실패했습니다.", e);
        }
    }
    
    @Override
    public List<PaymentPendingVO> getExpiredPaymentPending(LocalDateTime now) {
        return paymentPendingMapper.selectExpiredPaymentPending(now);
    }
    
    @Override
    public int calculateDiscount(List<Long> reservationIds) {
        // 간단한 할인 로직 (실제로는 더 복잡한 비즈니스 로직 적용)
        int totalPrice = calculateTotalPrice(reservationIds);
        
        // 3개 이상 예약 시 5% 할인
        if (reservationIds.size() >= 3) {
            return (int) (totalPrice * 0.05);
        }
        
        // 2개 예약 시 5% 할인
        if (reservationIds.size() >= 2) {
            return (int) (totalPrice * 0.05);
        }
        
        return 0;
    }
    
    // ===== Private Methods =====
    
    private int calculateTotalPrice(List<Long> reservationIds) {
        int totalPrice = 0;
        
        for (Long reservationId : reservationIds) {
            String reservationType = getReservationType(reservationId);
            
            if (PaymentPendingItemVO.TYPE_HOUSE.equals(reservationType)) {
                // 숙소 예약 가격 조회
                totalPrice += houseReservationService.getReservation(reservationId).getPrice();
            } else if (PaymentPendingItemVO.TYPE_CAR.equals(reservationType)) {
                // 차량 예약 가격 조회
                totalPrice += carService.getReservation(reservationId).getPrice();
            }
        }
        
        return totalPrice;
    }
    
    private String getReservationType(Long reservationId) {
        // 실제 예약 조회를 통해 타입 판별
        try {
            // 먼저 차량 예약에서 조회
            CarReservationVO carReservation = carService.getReservation(reservationId);
            if (carReservation != null) {
                return PaymentPendingItemVO.TYPE_CAR;
            }
            
            // 차량 예약이 없으면 숙소 예약에서 조회
            HouseReservationVO houseReservation = houseReservationService.getReservation(reservationId);
            if (houseReservation != null) {
                return PaymentPendingItemVO.TYPE_HOUSE;
            }
            
            // 둘 다 없으면 기본값 (숙소로 가정)
            log.warn("예약을 찾을 수 없음: reservationId={}", reservationId);
            return PaymentPendingItemVO.TYPE_HOUSE;
            
        } catch (Exception e) {
            log.error("예약 타입 판별 중 오류 발생: reservationId={}", reservationId, e);
            // 오류 발생 시 기본값 반환
            return PaymentPendingItemVO.TYPE_HOUSE;
        }
    }
    
    @Override
    public List<CarReservationVO> getUnpaidCarReservations(Long memberId) {
        try {
            // 결제되지 않은 차량 예약 조회 (상태가 RESERVED인 예약들)
            return carService.getReservationsByMember(memberId).stream()
                .filter(reservation -> "RESERVED".equals(reservation.getStatus()))
                .toList();
        } catch (Exception e) {
            log.error("결제되지 않은 차량 예약 조회 중 오류 발생: memberId={}", memberId, e);
            return List.of();
        }
    }
    
    @Override
    public List<HouseReservationVO> getUnpaidHouseReservations(Long memberId) {
        try {
            // 결제되지 않은 숙소 예약 조회 (상태가 0인 예약들)
            return houseReservationService.getReservationsByMember(memberId).stream()
                .filter(reservation -> reservation.getStatus() == 0)
                .toList();
        } catch (Exception e) {
            log.error("결제되지 않은 숙소 예약 조회 중 오류 발생: memberId={}", memberId, e);
            return List.of();
        }
    }
} 