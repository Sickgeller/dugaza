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
public class PaymentPendingVO {
    private Long paymentPendingId;
    private Long memberId;
    private Integer totalPrice;
    private Integer discountAmount;
    private Integer finalPrice;
    private Integer status; // 0: 대기, 1: 결제완료, 2: 취소, 3: 만료
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 상태 상수
    public static final int STATUS_PENDING = 0;
    public static final int STATUS_PAID = 1;
    public static final int STATUS_CANCELLED = 2;
    public static final int STATUS_EXPIRED = 3;
    
    // 상태 확인 메서드들
    public boolean isPending() {
        return status == STATUS_PENDING;
    }
    
    public boolean isPaid() {
        return status == STATUS_PAID;
    }
    
    public boolean isCancelled() {
        return status == STATUS_CANCELLED;
    }
    
    public boolean isExpired() {
        return status == STATUS_EXPIRED;
    }
    
    public boolean isExpired(LocalDateTime now) {
        return expiresAt != null && expiresAt.isBefore(now);
    }
} 