package kr.spring.api.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class HouseApiDto {

    private String addr1;
    private String addr2;
    private Long areaCode;
    private String cat1;
    private String cat2;
    private String cat3;
    private Long contentId;
    private Long contentTypeId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String firstImage;
    private String firstImage2;
    private String cpyrhtDivCd;
    private Double mapX;
    private Double mapY;
    private Long sigunguCode;
    private String tel;
    private String title;
    private Long zipcode;
    private Long mLevel;

}
