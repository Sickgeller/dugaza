package kr.spring.api.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AreaCodeApiDto {
    private Long areaCode;
    private String areaName;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

}
