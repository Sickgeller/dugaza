package kr.spring.api.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TourApiDto {
    private Long id;

    private String addr1;
    private String addr2;
    private Long areaCode;
    private String cat1;
    private String cat2;
    private String cat3;
    private Long contentId;
    private Long contentTypeId;
    private LocalDateTime createdTime;
    private LocalDateTime modifiedTime;
    private String firstImage;
    private String firstImage2;
    private String cpyrhtDivCd;
    private Double mapX;
    private Double mapY;
    private Integer mlevel;
    private Long sigunguCode;
    private String tel;
    private String title;
    private String zipcode;
    private Long showFlag; // 0이면 비활성화 1이면 활성화

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
