package kr.spring.api.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class EventDetailApiDto {
    private Long contentId;
    private Long contentTypeId; // 15 고정
    private String ageLimit;
    private String bookingPlace;
    private String discountInfoFestival;
    private LocalDateTime eventEndDate;
    private String eventHomePage;
    private String eventPlace;
    private LocalDateTime eventStartDate;
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
