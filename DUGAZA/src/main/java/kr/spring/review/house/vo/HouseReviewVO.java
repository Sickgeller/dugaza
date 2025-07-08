package kr.spring.review.house.vo;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public class HouseReviewVO {
    private Long reviewId;
    private Long memberId;
    private Long houseId;
    private Long roomId;
    private Long reservationId;
    private String reviewTitle;
    private String reviewContent;
    private Double rating;             // 0.0 ~ 5.0
    private LocalDateTime reviewDate;
    private int status;
}
