package kr.spring.payment.scheduler;

import kr.spring.payment.service.PaymentPendingService;
import kr.spring.payment.vo.PaymentPendingVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentPendingExpirationScheduler {
    
    private final PaymentPendingService paymentPendingService;
    
    /**
     * 1분마다 만료된 결제 대기 처리
     */
    @Scheduled(fixedRate = 60000)
    public void expirePaymentPending() {
        try {
            LocalDateTime now = LocalDateTime.now();
            List<PaymentPendingVO> expiredPayments = 
                paymentPendingService.getExpiredPaymentPending(now);
            
            log.info("만료된 결제 대기 처리 시작: {}개", expiredPayments.size());
            
            for (PaymentPendingVO payment : expiredPayments) {
                try {
                    paymentPendingService.expirePaymentPending(payment.getPaymentPendingId());
                    log.info("결제 대기 만료 처리 완료: paymentPendingId={}", payment.getPaymentPendingId());
                } catch (Exception e) {
                    log.error("결제 대기 만료 처리 실패: paymentPendingId={}", payment.getPaymentPendingId(), e);
                }
            }
            
            if (!expiredPayments.isEmpty()) {
                log.info("만료된 결제 대기 처리 완료: {}개", expiredPayments.size());
            }
            
        } catch (Exception e) {
            log.error("결제 대기 만료 스케줄러 실행 중 오류 발생", e);
        }
    }
} 