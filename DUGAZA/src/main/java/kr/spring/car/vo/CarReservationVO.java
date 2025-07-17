package kr.spring.car.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarReservationVO {
    private Long reservationId;
    private Long carId;
    private Long memberId;
    
    // 예약 일정 (데이터베이스 컬럼명에 맞춤) - TIMESTAMP 타입
    private LocalDateTime startDate;  // START_DATE (TIMESTAMP)
    private LocalDateTime endDate;    // END_DATE (TIMESTAMP)
    
    // 기존 필드들 (추가 정보용)
    private LocalTime pickupTime;
    private LocalTime returnTime;
    
    // 위치 정보
    private Integer pickupLocationCode;  // PICK_UP_LOCATION_CODE
    private Integer returnLocationCode;  // RETURN_LOCATION_CODE
    private String pickupLocation;
    private String returnLocation;
    
    // 운전자 정보
    private String driverName;
    private String driverLicense;
    private String phoneNumber;
    private String email;
    private String specialRequests;
    
    // 상태 및 시간
    private String status;  // STATUS
    private Date createdAt;  // CREATED_AT
    private Date updatedAt;  // UPDATED_AT
    
    // 가격 정보
    private Integer price;  // PRICE
    
    // JOIN 결과로 가져올 정보
    private CarVO car;
    private String carName;
    private String carType;
    private Integer carPrice;

    private String memberName;
} 