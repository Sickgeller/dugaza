package kr.spring.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExpressBusCityApiDto {
    private Long cityCode;
    private String cityName;
}
