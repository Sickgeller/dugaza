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
} 