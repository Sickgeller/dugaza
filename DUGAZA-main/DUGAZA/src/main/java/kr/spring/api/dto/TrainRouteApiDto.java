package kr.spring.api.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TrainRouteApiDto {
    private Long trainRouteId;
    private Long adultCharge; // 어른 요금
    private String depPlaceName; // 출발지 이름
    private String arrPlaceName; // 도착지 이름
    private LocalDateTime depPlandtime; // 출발시간
    private LocalDateTime arrPlandTime; // 도착시간
    private String trainGradeName; // 열차이름
    private Long trainNo; // 열차 번호
}
/*
CREATE TABLE TRAIN_ROUTE (
    TRAIN_ROUTE_ID   NUMBER PRIMARY KEY,               -- 노선 ID
    ADULT_CHARGE     NUMBER,                           -- 어른 요금
    ARR_PLACE_NAME   VARCHAR2(100),                    -- 도착지 이름
    DEP_PLACE_NAME   VARCHAR2(100),                    -- 출발지 이름
    ARR_PLAND_TIME   TIMESTAMP,                        -- 도착 시간
    DEP_PLAND_TIME   TIMESTAMP,                        -- 출발 시간
    TRAIN_GRADE_NAME VARCHAR2(100),                    -- 열차 이름
    TRAIN_NO         NUMBER                            -- 열차 번호
);
 */