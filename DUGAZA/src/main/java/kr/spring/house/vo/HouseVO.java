package kr.spring.house.vo;

import kr.spring.tour.vo.TourVO;
import lombok.Data;
/*
 * House_detail 자바빈 - 여행 콘텐츠 상속 받음
 * */
@Data
public class HouseVO extends TourVO{
	private Integer accomCountLodging;       // 수용 가능 인원
    private String checkInTime;              // 입실 시간
    private String checkOutTime;             // 퇴실 시간
    private String chkCooking;               // 객실 내 취사 여부
    private String foodPlace;                // 식음료장
    private String infoCenterLodging;        // 문의 및 안내
    private String parkingLodging;           // 주차 시설
    private String pickup;                   // 픽업 서비스
    private Integer roomCount;               // 객실 수
    private String reservationLodging;       // 예약 안내
    private String reservationUrl;           // 예약 안내 홈페이지
    private String roomType;                 // 객실 유형
    private String scaleLodging;             // 규모
    private String subFacility;              // 부대시설 (기타)

    // 추가 시설 여부 (Yes/No 형식 또는 기타 상세값)
    private String barbecue;                 // 바비큐장 여부
    private String beauty;                   // 뷰티 시설
    private String beverage;                 // 식음료장 여부
    private String bicycle;                  // 자전거 대여 여부
    private String campfire;                 // 캠프파이어 여부
    private String fitness;                  // 휘트니스 센터 여부
    private String karaoke;                 // 노래방 여부
    private String publicBath;              // 공용 샤워실 여부
    private String publicPc;                // 공용 PC실 여부
    private String sauna;                   // 사우나 여부
    private String seminar;                 // 세미나실 여부
    private String sports;                  // 스포츠 시설 여부

    private String refundRegulation;        // 환불 규정
}
