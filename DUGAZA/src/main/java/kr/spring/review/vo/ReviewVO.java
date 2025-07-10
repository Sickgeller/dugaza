package kr.spring.review.vo;

import java.sql.Date;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReviewVO {
	private Long reviewId;          // 리뷰 ID
	private Long memberId;          // 작성자 회원 ID
	private Long contentId;         // 컨텐츠 ID
	private Long reservationId;     // 예약 ID (nullable)
	private Double rating;          // 평점 (소수점 1자리)
	private Integer status;         // 상태 (1: 정상, 0: 삭제 등)
	private String review;          // 리뷰 내용
	private Date createdAt;         // 생성일시
	private Date updatedAt;         // 수정일시
	private Long contentTypeId;     // 컨텐츠 타입 ID
}
