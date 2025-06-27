package kr.spring.common;

import lombok.Getter;

@Getter
public enum TrainHubStationEnum {
    POHANG("NAT8B0351", "포항", 1, 37L),
    GIMCHEON_GUMI("NATH12383", "김천구미", 1, 37L),
    BUSAN("NAT014445", "부산", 1, 21L),
    CHEONGNYANGNI("NAT130126", "청량리", 1, 11L),
    SEOUL("NAT010000", "서울", 1, 11L),
    YONGSAN("NAT010032", "용산", 1, 11L),
    DONGDAEGU("NAT013271", "동대구", 1, 22L),
    GWANGJU_SONGJEONG("NAT031857", "광주송정", 1, 24L),
    DAEJEON("NAT011668", "대전", 1, 25L),
    SUWON("NAT010415", "수원", 1, 31L),
    GANGNEUNG("NAT601936", "강릉", 1, 32L),
    JECHEON("NAT021549", "제천", 1, 33L),
    OSONG("NAT050044", "오송", 1, 33L),
    CHEONAN_ASAN("NATH10960", "천안아산", 1, 34L),
    IKSAN("NAT030879", "익산", 1, 35L),
    MOKPO("NAT032563", "목포", 1, 36L),
    SOONCHEON("NAT041595", "순천", 1, 36L);

    private final String nodeId;
    private final String nodeName;
    private final int isActive;
    private final Long cityCode;

    TrainHubStationEnum(String nodeId, String nodeName, int isActive, Long cityCode) {
        this.nodeId = nodeId;
        this.nodeName = nodeName;
        this.isActive = isActive;
        this.cityCode = cityCode;
    }
}
