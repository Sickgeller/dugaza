package kr.spring.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RestaurantDetailApiDto {
    private Long contentId;
    private Long contentTypeId; // 39 고정
    private String chkCreditCardFood;
    private String discountInfoFood;
    private String firstMenu;
    private String infoCenterFood;
    private String kidsFacility;
    private String openDateFood;
    private String openTimeFood;
    private String packing;
    private String parkingFood;
    private String reservationFood;
    private String restDateFood;
    private String scaleFood;
    private int seat;
    private String smoking;
    private String treatMenu;
    private String lcnsNo;
}
