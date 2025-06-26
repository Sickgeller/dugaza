package kr.spring.api.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SigunguCodeApiDto {
    private Long areaCode;
    private Long sigunguCode;
    private String sigunguName;
    private Long isActive;
}
