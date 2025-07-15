package kr.spring.car.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarRegisterDTO {
    private String carName;
    private String carType;
    private String carNumber;
    private Integer carYear;
    private String carColor;
    private String carFuel;
    private Integer carSeats;
    private Integer carPrice;
    private MultipartFile carImage;
} 