package kr.spring.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ShoppingDetailApiDto {
    private Long contentId;
    private Long contentTypeId; // 38 고정
    private String chkBabyCarriageShopping;
    private String chkCreditCardShopping;
    private String chkPetShopping;
    private String cultureCenter;
    private String fairDay;
    private String infoCenterShopping;
    private String openDateShopping;
    private String openTime;
    private String parkingShopping;
    private String restDateShopping;
    private String restroom;
    private String saleItem;
    private String saleItemCost;
    private String scaleShopping;
    private String shopGuide;
}