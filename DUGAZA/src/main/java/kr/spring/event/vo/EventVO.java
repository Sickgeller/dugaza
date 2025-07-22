package kr.spring.event.vo;

import java.sql.Date;

import kr.spring.tour.vo.TourVO;
import lombok.Data;

@Data
public class EventVO extends TourVO{
	private Integer ageLimit;
    private String bookingPlace;
    private String discountInfoFestival;
    private Date eventEndDate;
    private String eventHomepage;
    private String eventPlace;
    private Date eventStartDate;
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
}
