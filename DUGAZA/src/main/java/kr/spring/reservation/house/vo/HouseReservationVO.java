package kr.spring.reservation.house.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HouseReservationVO {
    private Long reservationId;
    private Long memberId;
    private Long houseId;
    private Long roomId;  // roomNo를 roomId로 변경
    private String roomName;  // 추가
    private Long roomPrice;   // 추가
    private LocalDateTime reservationStartDate;
    private LocalDateTime reservationEndDate;
    private Long reservationCount;
    private Long price;
    private String status;    // int에서 String으로 변경
}
