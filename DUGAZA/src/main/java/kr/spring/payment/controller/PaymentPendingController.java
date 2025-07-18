package kr.spring.payment.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.spring.auth.security.CustomUserDetails;
import kr.spring.payment.service.PaymentPendingService;
import kr.spring.payment.vo.PaymentPendingItemVO;
import kr.spring.payment.vo.PaymentPendingVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/payment-pending")
@RequiredArgsConstructor
@Slf4j
public class PaymentPendingController {
    
    private final PaymentPendingService paymentPendingService;
    
    /**
     * 결제 대기 생성
     */
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createPaymentPending(
            @RequestBody Map<String, Object> request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            if (userDetails == null || userDetails.getMember() == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "로그인이 필요합니다."
                ));
            }
            
            Long memberId = userDetails.getMember().getMemberId();
            
            @SuppressWarnings("unchecked")
            List<Object> rawList = (List<Object>) request.get("reservationIds");

            List<Long> reservationIds = rawList.stream()
                .map(obj -> {
                    if (obj instanceof Number) {
                        return ((Number) obj).longValue();
                    } else {
                        return Long.parseLong(obj.toString());
                    }
                })
                .collect(Collectors.toList());
            
            if (reservationIds == null || reservationIds.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "예약 ID 목록이 필요합니다."
                ));
            }
            
            PaymentPendingVO paymentPending = paymentPendingService.createPaymentPending(memberId, reservationIds);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "결제 대기가 생성되었습니다.",
                "paymentPendingId", paymentPending.getPaymentPendingId(),
                "finalPrice", paymentPending.getFinalPrice(),
                "expiresAt", paymentPending.getExpiresAt()
            ));
            
        } catch (Exception e) {
            log.error("결제 대기 생성 중 오류 발생", e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "결제 대기 생성에 실패했습니다: " + e.getMessage()
            ));
        }
    }
    
    /**
     * 결제 대기 조회
     */
    @GetMapping("/{paymentPendingId}")
    public ResponseEntity<Map<String, Object>> getPaymentPending(@PathVariable Long paymentPendingId) {
        try {
            PaymentPendingVO paymentPending = paymentPendingService.getPaymentPending(paymentPendingId);
            
            if (paymentPending == null) {
                return ResponseEntity.notFound().build();
            }
            
            List<PaymentPendingItemVO> items = paymentPendingService.getPaymentPendingItems(paymentPendingId);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "paymentPending", paymentPending,
                "items", items
            ));
            
        } catch (Exception e) {
            log.error("결제 대기 조회 중 오류 발생: paymentPendingId={}", paymentPendingId, e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "결제 대기 조회에 실패했습니다."
            ));
        }
    }
    
    /**
     * 회원의 결제 대기 목록 조회
     */
    @GetMapping("/member")
    public ResponseEntity<Map<String, Object>> getPaymentPendingByMember(@AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            if (userDetails == null || userDetails.getMember() == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "로그인이 필요합니다."
                ));
            }
            
            Long memberId = userDetails.getMember().getMemberId();
            
            List<PaymentPendingVO> paymentPendings = paymentPendingService.getPaymentPendingByMember(memberId);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "paymentPendings", paymentPendings
            ));
            
        } catch (Exception e) {
            log.error("회원 결제 대기 목록 조회 중 오류 발생", e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "결제 대기 목록 조회에 실패했습니다."
            ));
        }
    }
    
    /**
     * 결제 처리
     */
    @PostMapping("/{paymentPendingId}/process")
    public ResponseEntity<Map<String, Object>> processPayment(@PathVariable Long paymentPendingId) {
        try {
            paymentPendingService.processPayment(paymentPendingId);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "결제가 완료되었습니다."
            ));
            
        } catch (Exception e) {
            log.error("결제 처리 중 오류 발생: paymentPendingId={}", paymentPendingId, e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "결제 처리에 실패했습니다: " + e.getMessage()
            ));
        }
    }
    
    /**
     * 결제 대기 취소
     */
    @PostMapping("/{paymentPendingId}/cancel")
    public ResponseEntity<Map<String, Object>> cancelPaymentPending(@PathVariable Long paymentPendingId) {
        try {
            paymentPendingService.cancelPaymentPending(paymentPendingId);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "결제 대기가 취소되었습니다."
            ));
            
        } catch (Exception e) {
            log.error("결제 대기 취소 중 오류 발생: paymentPendingId={}", paymentPendingId, e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "결제 대기 취소에 실패했습니다: " + e.getMessage()
            ));
        }
    }
} 