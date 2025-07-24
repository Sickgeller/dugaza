package kr.spring.common;

import lombok.Getter;

@Getter
public enum ContentTypeid {
    TOURIST_ATTRACTION(12), // 관광명소
    CULTURAL_CENTER(14), // 문화시설
    EVENT(15), // 행사 및 축제
    TRIP_COURSE(25), // 여행코스
    LEPORTS(28), // 레포츠
    HOUSE(32), // 숙소
    SHOPPING(38), // 쇼핑
    RESTAURANT(39); // 식당

    private final int code;

    ContentTypeid(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static ContentTypeid fromCode(int code) {
        for (ContentTypeid type : ContentTypeid.values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        return null;
    }
}
