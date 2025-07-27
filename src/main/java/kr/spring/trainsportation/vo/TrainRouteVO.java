package kr.spring.trainsportation.vo;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class TrainRouteVO {
    private Integer trainRouteId;
    private Integer adultCharge;
    private String arrPlaceName;
    private String depPlaceName;
    private LocalDateTime arrPlandTime;
    private LocalDateTime depPlandTime;
    private String trainGradeName;
    private Integer trainNo;
} 