package kr.spring.payment.vo;

import lombok.Data;

@Data
public class PaymentRequest {
	private String paymentKey;
	private String orderId;
	private int amount;
}
