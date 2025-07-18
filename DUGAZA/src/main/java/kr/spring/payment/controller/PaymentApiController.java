package kr.spring.payment.controller;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import kr.spring.payment.dto.PaymentRequestDTO;
import kr.spring.payment.dto.PaymentResponseDTO;
import kr.spring.payment.service.PaymentPendingService;
import kr.spring.payment.service.PaymentService;
import kr.spring.payment.vo.PaymentRequest;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/payment") // API 경로를 명확히 분리
@RequiredArgsConstructor
@Slf4j
public class PaymentApiController {

    private final PaymentService paymentService;
    private final PaymentPendingService paymentPendingService;
    
    @org.springframework.beans.factory.annotation.Value("${toss.payments.secret-key}")
    private String secretKey;

    /**
     * 토스페이먼츠 결제 승인 요청
     */
    @PostMapping("/confirm")
	public ResponseEntity<?> confirmPayment(@RequestBody PaymentRequest paymentRequest) {
		try {
			String secretKey = this.secretKey;
			String encodedKey = Base64.getEncoder().encodeToString((secretKey + ":").getBytes());

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.set("Authorization", "Basic " + encodedKey);

			// 요청 본문
			Map<String, Object> body = new HashMap<>();
			body.put("paymentKey", paymentRequest.getPaymentKey());
			body.put("orderId", paymentRequest.getOrderId());
			body.put("amount", paymentRequest.getAmount());

			HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);

			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<String> response = restTemplate.postForEntity(
					"https://api.tosspayments.com/v1/payments/confirm",
					requestEntity,
					String.class
					);

			return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
		} catch (HttpClientErrorException e) {
			return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
		}
	}

    /**
     * 결제 대기 취소
     */
    @PostMapping("/pending/cancel/{paymentPendingId}")
    public Map<String, Object> cancelPaymentPending(@PathVariable Long paymentPendingId) {
        try {
            paymentPendingService.cancelPaymentPending(paymentPendingId);
            return Map.of("success", true, "message", "결제 대기가 취소되었습니다.");
        } catch (Exception e) {
            log.error("결제 대기 취소 중 오류 발생", e);
            return Map.of("success", false, "message", "결제 대기 취소에 실패했습니다.");
        }
    }
}