package kr.spring.reservation.event.vo;

import lombok.Data;

@Data
public class EventReservationVO {
	private Long eventId;
	private Long userId;
	private Integer guestCount;
	private Integer totalPrice;
	private String paymentStatus;
}
