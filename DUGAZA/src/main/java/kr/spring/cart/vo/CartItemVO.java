package kr.spring.cart.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemVO {
    private String cartItemId;  // 장바구니 아이템 고유 ID
    private Long memberId;      // 회원 ID
    private String itemType;    // ITEM_TYPE: HOUSE, CAR
    private Long itemId;        // 숙소 ID 또는 차량 ID
    
    // 예약 정보
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer quantity;   // 숙소의 경우 인원수, 차량의 경우 1
    private Integer unitPrice;  // 일일 요금
    private Integer totalPrice; // 총 요금
    
    // 아이템 정보 (JOIN 결과)
    private String itemName;    // 숙소명 또는 차량명
    private String itemImage;   // 이미지 URL
    private String itemTypeName; // 숙소 타입 또는 차량 타입
    
    // 추가 정보
    private String pickupLocation;  // 차량 수령 장소 (차량인 경우)
    private String returnLocation;  // 차량 반납 장소 (차량인 경우)
    private String roomType;        // 객실 타입 (숙소인 경우)
    
    // 시간 정보
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 