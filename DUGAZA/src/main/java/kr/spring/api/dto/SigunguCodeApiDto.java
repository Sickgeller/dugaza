package kr.spring.api.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SigunguCodeApiDto {
    private Long sigunguCode;
    private Long parentCode;
    private String sigunguName;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
