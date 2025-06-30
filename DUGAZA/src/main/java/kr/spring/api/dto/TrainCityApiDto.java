package kr.spring.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TrainCityApiDto {
    private Long cityCode;
    private String cityName;
}

/**
 * CREATE TABLE TRAIN_CITY(
 *      CITY_CODE NUMBER PRIMARY KEY,
 *      CITY_NAME VARCHAR2(20)
 * )
 */
