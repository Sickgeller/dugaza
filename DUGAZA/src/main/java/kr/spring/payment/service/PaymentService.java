package kr.spring.payment.service;

import kr.spring.payment.vo.PaymentPendingVO;
import kr.spring.payment.dto.PaymentRequestDTO;
import kr.spring.payment.dto.PaymentResponseDTO;

public interface PaymentService {
    
    /**
     * 결제 처리
     * @param paymentPending 결제 대기 정보
     * @param paymentRequest 결제 요청 정보
     * @return 결제 응답 정보
     */
    PaymentResponseDTO processPayment(PaymentPendingVO paymentPending, PaymentRequestDTO paymentRequest);
    
    /**
     * 결제 검증
     * @param paymentPending 결제 대기 정보
     * @param paymentRequest 결제 요청 정보
     * @return 검증 성공 여부
     */
    boolean validatePayment(PaymentPendingVO paymentPending, PaymentRequestDTO paymentRequest);
    
    /**
     * 결제 취소
     * @param paymentId 결제 ID
     * @return 취소 성공 여부
     */
    boolean cancelPayment(String paymentId);
    
    /**
     * 결제 상태 조회
     * @param paymentId 결제 ID
     * @return 결제 상태
     */
    String getPaymentStatus(String paymentId);
} 