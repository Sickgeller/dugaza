package kr.spring.seller.vo;

import lombok.Getter;

@Getter
public enum SellerRole {

    SELLER("SELLER"),
    CAR_SELLER("CAR_SELLER"),
    HOUSE_SELLER("HOUSE_SELLER"),
    ADMIN_SELLER("ADMIN_SELLER");

    private String value;

    SellerRole(String value){
        this.value = value;
    }

}
