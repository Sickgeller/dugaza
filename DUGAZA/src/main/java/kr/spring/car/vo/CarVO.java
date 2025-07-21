package kr.spring.car.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarVO {
    private Long carId;
    private Long sellerId;
    private String carName;
    private String carType;
    private String carNumber;
    private Integer carYear;
    private String carColor;
    private String carFuel;
    private Integer carSeats;
    private Integer carPrice;
    private String carImage;
    private String status;
    
    // 위치 정보 (코드 기반)
    private Integer locationCode;        // 기본 위치 코드 (1-8)
    
    // JOIN 결과로 가져올 위치 정보
    private String locationName;         // 위치명
    
    // JOIN 결과로 가져올 판매자 정보
    private String sellerName;           // 판매자명
    
    // 리뷰 목록
    private List<CarReviewVO> reviews;
    
    private Date createdAt;
    private Date updatedAt;
} 