package kr.spring.car.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class CarReservationDTO {
    private Long carId;
    private Long memberId;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate pickupDate;
    
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime pickupTime;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate returnDate;
    
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime returnTime;
    
    private Integer pickupLocationCode;  // 수령 장소 코드
    private Integer returnLocationCode;  // 반납 장소 코드
    private String pickupLocation;       // 수령 장소명 (화면 표시용)
    private String returnLocation;       // 반납 장소명 (화면 표시용)
    private String driverName;
    private String driverLicense;
    private String phoneNumber;
    private String email;
    private String specialRequests;
} 