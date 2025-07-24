package kr.spring.payment.dao;

import kr.spring.payment.vo.PaymentPendingVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface PaymentPendingMapper {
    
    /**
     * 결제 대기 생성
     */
    void insertPaymentPending(PaymentPendingVO paymentPending);
    
    /**
     * 결제 대기 수정
     */
    void updatePaymentPending(PaymentPendingVO paymentPending);
    
    /**
     * 결제 대기 조회
     */
    PaymentPendingVO selectPaymentPending(@Param("paymentPendingId") Long paymentPendingId);
    
    /**
     * 회원의 결제 대기 목록 조회
     */
    List<PaymentPendingVO> selectPaymentPendingByMember(@Param("memberId") Long memberId);
    
    /**
     * 만료된 결제 대기 목록 조회
     */
    List<PaymentPendingVO> selectExpiredPaymentPending(@Param("now") LocalDateTime now);
    
    /**
     * 결제 대기 상태 업데이트
     */
    void updateStatus(@Param("paymentPendingId") Long paymentPendingId, 
                     @Param("status") Integer status);
} 