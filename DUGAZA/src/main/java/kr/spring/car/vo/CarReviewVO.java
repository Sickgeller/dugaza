package kr.spring.car.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarReviewVO {
    private Long reviewId;
    private Long carId;
    private Long memberId;
    private Integer rating;
    private String content;
    private Date createdAt;
} 