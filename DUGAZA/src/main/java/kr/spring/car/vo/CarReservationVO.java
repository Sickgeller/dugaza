package kr.spring.car.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
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
    
    // 예약 일정
    private LocalDate pickupDate;
    private LocalTime pickupTime;
    private LocalDate returnDate;
    private LocalTime returnTime;
    
    // 위치 정보
    private Integer pickupLocationCode;
    private Integer returnLocationCode;
    private String pickupLocation;
    private String returnLocation;
    
    // 운전자 정보
    private String driverName;
    private String driverLicense;
    private String phoneNumber;
    private String email;
    private String specialRequests;
    
    // 상태 및 시간
    private String status;  // RESERVED, CANCELLED, COMPLETED
    private Date createdAt;
    private Date updatedAt;
    
    // JOIN 결과로 가져올 정보
    private CarVO car;
    private String carName;
    private String carType;
    private Integer carPrice;
} 