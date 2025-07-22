package kr.spring.api.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TrainKindApiDto {
    private String vehicleKindId;
    private String vehicleKindNm;
    private String isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
