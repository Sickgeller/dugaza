package kr.spring.car.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarLocationCodeVO {
    private Integer locationCode;    // 위치 코드 (1-8)
    private String locationName;     // 위치명 (서울, 부산, 대구 등)
    private String locationDetail;   // 상세 위치 (서울 (강남역), 부산 (부산역) 등)
    private String isActive;         // 활성화 여부 (Y:활성, N:비활성)
    private Date createdAt;          // 생성일시
} 