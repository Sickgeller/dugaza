package kr.spring.reservation.house.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
public class HouseReservationVO {
    private Long reservationId;
    private Long memberId;
    private Long houseId;
    private Long roomId;
    private LocalDateTime reservationStart;
    private LocalDateTime reservationEnd;
    private int reservationCount;
    private double price;
    private int status;
}
