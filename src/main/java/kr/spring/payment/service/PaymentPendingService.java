package kr.spring.payment.service;

import kr.spring.payment.vo.PaymentPendingVO;
import kr.spring.payment.vo.PaymentPendingItemVO;
import kr.spring.car.vo.CarReservationVO;
import kr.spring.reservation.vo.HouseReservationVO;

import java.time.LocalDateTime;
import java.util.List;

public interface PaymentPendingService {
    
    /**
     * 결제 대기 생성
     */
    PaymentPendingVO createPaymentPending(Long memberId, List<Long> reservationIds);
    
    /**
     * 결제 대기 조회
     */
    PaymentPendingVO getPaymentPending(Long paymentPendingId);
    
    /**
     * 회원의 결제 대기 목록 조회
     */
    List<PaymentPendingVO> getPaymentPendingByMember(Long memberId);
    
    /**
     * 결제 대기 아이템 목록 조회
     */
    List<PaymentPendingItemVO> getPaymentPendingItems(Long paymentPendingId);
    
    /**
     * 결제 처리
     */
    void processPayment(Long paymentPendingId);
    
    /**
     * 결제 대기 취소
     */
    void cancelPaymentPending(Long paymentPendingId);
    
    /**
     * 만료된 결제 대기 처리
     */
    void expirePaymentPending(Long paymentPendingId);
    
    /**
     * 만료된 결제 대기 목록 조회
     */
    List<PaymentPendingVO> getExpiredPaymentPending(LocalDateTime now);
    
    /**
     * 할인 금액 계산
     */
    int calculateDiscount(List<Long> reservationIds);
    
    /**
     * 회원의 결제되지 않은 예약 목록 조회 (차량)
     */
    List<CarReservationVO> getUnpaidCarReservations(Long memberId);
    
    /**
     * 회원의 결제되지 않은 예약 목록 조회 (숙소)
     */
    List<HouseReservationVO> getUnpaidHouseReservations(Long memberId);
} 