package kr.spring.seller.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HouseSellerDetailVO {
    private Long sellerId;
    private Long houseId;
    private String status; // available, inavailable, suspending, deleted
    
    // 추가 필드 (매퍼에서 사용)
    private String houseTitle;
    private String houseAddress;
    private String sellerName;
    private String sellerEmail;
} 