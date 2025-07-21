package kr.spring.trainsportation.vo;

import lombok.Data;

@Data
public class TrainStationVO {
    private String nodeId;
    private String nodeName;
    private String isActive;
    private Integer cityCode;
} 