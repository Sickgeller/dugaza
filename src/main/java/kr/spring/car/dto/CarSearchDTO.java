package kr.spring.car.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class CarSearchDTO {
    private Integer pickupLocationCode;  // 수령 장소 코드 (1-8)
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate pickupDate;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate returnDate;
    
    private String carType;
    private Integer minSeats;
    private Integer maxPrice;
    private String fuelType;
    
    // 위치명 (화면 표시용)
    private String pickupLocationName;
} 