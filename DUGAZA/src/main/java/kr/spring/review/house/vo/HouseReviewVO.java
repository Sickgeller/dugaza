package kr.spring.review.house.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class HouseReviewVO {
    private Long reviewId;
    private Long memberId;
    private Long houseId;
    private Long roomId;
    private Long reservationId;
    private String reviewTitle;
    private String reviewContent;
    private Double rating;
    private LocalDateTime reviewDate;
    private int status;
    

}
