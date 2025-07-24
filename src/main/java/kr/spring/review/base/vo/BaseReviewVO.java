package kr.spring.review.base.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/*
    여러 리뷰 사이에 있는 공통사항
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BaseReviewVO {

    // 기본 리뷰 정보 (테이블 구조와 정확히 일치)
    private Long reviewId; // REVIEW_ID
    private Long memberId; // MEMBER_ID
    private Long contentId; // CONTENT_ID
    private Long reservationId; // RESERVATION_ID
    private Double rating; // RATING
    private Integer status; // STATUS
    private String review; // REVIEW
    private LocalDateTime createdAt; // CREATED_AT
    private LocalDateTime updatedAt; // UPDATED_AT
    private Long contentTypeId; // CONTENT_TYPE_ID

    // 조인할때 쓸것
    private String id; // 조인한 멤버 이름
    
}
