package kr.spring.tour.vo;

import java.sql.Date;

import lombok.Data;
/*
 * tour_content 자바빈 - 여행 항목들의 슈퍼 클래스
 * */
@Data
public class TourVO {
	private Long id;                   // DB 고유 ID
    private String addr1;             // 주소
    private String addr2;             // 상세주소
    private Integer areaCode;         // 지역 코드
    private String cat1;              // 서비스 대분류
    private String cat2;              // 서비스 중분류
    private String cat3;              // 서비스 소분류
    private Long contentId;           // 콘텐츠 고유 식별자
    private Integer contentTypeId;    // 콘텐츠 타입 ID (32: 숙박 등)
    private String firstImage;        // 큰 이미지 URL
    private String firstImage2;       // 썸네일 이미지 URL
    private String cpyrhtDivCd;       // 저작권 코드
    private Double mapX;              // 지도 X좌표
    private Double mapY;              // 지도 Y좌표
    private Integer mlevel;           // 지도 레벨
    private Integer sigunguCode;      // 시군구 코드
    private String tel;               // 전화번호
    private String title;             // 콘텐츠 이름
    private String zipcode;           // 우편번호
    private Date createdAt;  		  // 생성일
    private Date updatedAt;  		  // 수정일
    private Integer showFlag;         // 표시 여부 (1: 표시, 0: 비표시)
    private String overview;          // 개요
    
    // 찜 상태 필드 추가
    private boolean wished;           // 찜 여부
    private Integer review_count;     // 리뷰 수
    private Integer countReview;
    private Double avgReview;
    private Double review_avg;        // 리뷰 평점
    private Long price;               // 가격
    
    public String getContentTypeName() {
        return ContentType.getNameById(this.contentTypeId);
    }
}
