package kr.spring.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LeportsDetailApiDto {

    private Long contentId;
    private Long contentTypeId; // 28 고정
    private Long accomCountLeports;
    private String chkBabyCarriageLeports;
    private String chkCreditCardLeports;
    private String chkPetLeports;
    private String expAgeRangeLeports;
    private String infoCenterLeports;
    private String openPeriod;
    private String parkingFeeLeports;
    private String parkingLeports;
    private String reservation;
    private String restDateLeports;
    private String scaleLeports;
    private String useFeeLeports;
    private String useTimeLeports;
}
