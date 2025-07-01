package kr.spring.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CulturalCenterDetailApiDto {
    private Long contentId;
    private Long contentTypeId; // 14 고정
    private Long accomCountCulture;
    private String chkBabyCarriageCulture;
    private String chkCreditCardCulture;
    private String chkPetCulture;
    private String discountInfo;
    private String infoCenterCulture;
    private String parkingCulture;
    private String parkingFee;
    private String restDateCulture;
    private String useFee;
    private String useTimeCulture;
    private String scale;
    private String spendTime;
    private String ageLimit;
}
