package kr.spring.common;

public enum SellerType {

    GENERAL("GENERAL"),
    CAR("CAR"),
    HOUSE("HOUSE"),
    RESTAURANT("RESTAURANT"),
    TOUR("TOUR");

    private final String value;

    SellerType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
