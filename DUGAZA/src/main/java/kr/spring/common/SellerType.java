package kr.spring.common;

public enum SellerType {

    CAR("CAR"),
    HOUSE("HOUSE");

    private final String value;

    SellerType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
