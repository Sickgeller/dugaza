package kr.spring.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TrainStationApiDto {
    private String nodeId; // 역코드
    private String nodeName; // 역이름
    private Long cityCode;
    private String isActive;
}

/**
 * CREATE TABLE TRAIN_STATION(
 *      NODE_ID VARCHAR2(30) PRIMARY KEY,
 *      NODE_NAME VARCHAR2(30),
 *      IS_ACTIVE VARCHAR2(5) DEFAULT 'Y'
 * )
 */
