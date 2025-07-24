package kr.spring.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseDTO {
    private String paymentId;
    private String status; // SUCCESS, FAILED, CANCELLED
    private String message;
    private Integer amount;
    private String currency;
    private LocalDateTime paymentDate;
    private String transactionId;
    private String cardLastFourDigits;
    private String cardType;
} 