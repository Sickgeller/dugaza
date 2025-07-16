package kr.spring.payment.dao;

import kr.spring.payment.vo.PaymentPendingItemVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PaymentPendingItemMapper {
    
    /**
     * 결제 대기 아이템 추가
     */
    void insertPaymentPendingItem(PaymentPendingItemVO item);
    
    /**
     * 결제 대기 아이템 삭제
     */
    void deletePaymentPendingItem(@Param("paymentPendingId") Long paymentPendingId, 
                                 @Param("reservationId") Long reservationId);
    
    /**
     * 결제 대기의 모든 아이템 삭제
     */
    void deletePaymentPendingItems(@Param("paymentPendingId") Long paymentPendingId);
    
    /**
     * 결제 대기 아이템 목록 조회
     */
    List<PaymentPendingItemVO> selectPaymentPendingItems(@Param("paymentPendingId") Long paymentPendingId);
    
    /**
     * 예약 ID로 결제 대기 아이템 조회
     */
    PaymentPendingItemVO selectPaymentPendingItemByReservation(@Param("reservationId") Long reservationId);
} 