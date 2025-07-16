package kr.spring.payment.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentPendingItemVO {
    private Long paymentPendingId;
    private Long reservationId;
    private String reservationType; // 'HOUSE', 'CAR'
    private LocalDateTime createdAt;
    
    // 예약 타입 상수
    public static final String TYPE_HOUSE = "HOUSE";
    public static final String TYPE_CAR = "CAR";
    
    // 타입 확인 메서드들
    public boolean isHouse() {
        return TYPE_HOUSE.equals(reservationType);
    }
    
    public boolean isCar() {
        return TYPE_CAR.equals(reservationType);
    }
} 