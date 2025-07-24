package kr.spring.tour.vo;

import lombok.Data;

@Data
public class CulturalCenterVO extends TourVO{
	private String accomCountCulture;         // 수용 가능 인원
    private String chkBabyCarriageCulture;    // 유모차 가능 여부
    private String chkCreditCardCulture;      // 신용카드 가능 여부
    private String chkPetCulture;             // 반려동물 동반 여부
    private String discountInfo;              // 할인 정보
    private String infoCenterCulture;         // 안내소 정보
    private String parkingCulture;            // 주차 가능 여부
    private String parkingFee;                // 주차 요금
    private String restDateCulture;           // 쉬는 날
    private String usefee;                    // 이용 요금
    private String scale;                     // 규모
    private String useTimeCulture;            // 이용 시간
    private String spendTime;                 // 소요 시간
}
