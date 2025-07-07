package kr.spring.tour.vo;

import lombok.Data;

@Data
public class TouristAttractionVO extends TourVO{
	private String accomCount;           // 수용 인원
	private String chkBabyCarriage;      // 유모차 가능 여부
	private String chkCreditCard;        // 신용카드 가능 여부
	private String chkPet;               // 반려동물 동반 여부
	private String expageRange;          // 체험 가능 연령
	private String expGuide;             // 체험 안내
	private Integer heritage1;           // 문화재 유형 1
	private Integer heritage2;           // 문화재 유형 2
	private Integer heritage3;           // 문화재 유형 3
	private String infoCenter;           // 관광안내소 정보
	private String openDate;             // 개장일
	private String parking;              // 주차 정보
	private String restDate;             // 휴무일
	private String useSeason;            // 이용 가능 계절
	private String useTime;              // 이용 시간
}
