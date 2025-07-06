package kr.spring.api.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
public class SigunguCodeApiDto {
    private Long areaCode;
    private Long sigunguCode;
    private String sigunguName;
    private Long isActive;
}
