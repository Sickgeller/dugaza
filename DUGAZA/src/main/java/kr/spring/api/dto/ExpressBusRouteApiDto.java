package kr.spring.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExpressBusRouteApiDto {
    private String depPlaceNm;       // 출발지명
    private String arrPlaceNm;       // 도착지명
    private String depPlandTimeStr;  // 출발시간 (HH:mm 형식)
    private String arrPlandTimeStr;  // 도착시간 (HH:mm 형식)
    private String charge;           // 요금
    private String gradeNm;          // 등급명 (우등, 일반, 프리미엄)
    private String routeId;          // 노선ID
} 