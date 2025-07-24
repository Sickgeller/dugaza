package kr.spring.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TrainStationApiDto {
    private String nodeId; // 역코드
    private String nodeName; // 역이름
    private Long cityCode; // 도시 코드
    @Builder.Default
    private String isActive = "Y"; // 활성화 여부 (기본값 'Y')
}

/**
 * CREATE TABLE TRAIN_STATION(
 *      NODE_ID VARCHAR2(30) PRIMARY KEY,
 *      NODE_NAME VARCHAR2(30),
 *      CITY_CODE NUMBER,
 *      IS_ACTIVE VARCHAR2(5) DEFAULT 'Y'
 * )
 */
