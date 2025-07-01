package kr.spring.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EventDetailApiDto {
    private Long contentId;
    private Long contentTypeId; // 15 고정
    private String ageLimit;
    private String bookingPlace;
    private String discountInfoFestival;
    private String eventEndDate;
    private String eventHomePage;
    private String eventPlace;
    private String eventStartDate;
    private String festivalGrade;
    private String placeInfo;
    private String playTime;
    private String program;
    private String spendTimeFestival;
    private String sponsor1;
    private String sponsor1Tel;
    private String sponsor2;
    private String sponsor2Tel;
    private String subEvent;
    private String useTimeFestival;

    // getters and setters

}
