package kr.spring.payment.dto;

import lombok.Data;

@Data
public class PaymentRequestDTO {
    private String cardNumber;
    private String expiryDate;
    private String cvc;
    private String cardHolder;
    private String birthDate;
    private String paymentMethod = "CARD";
    private String currency = "KRW";
} 