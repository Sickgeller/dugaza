package kr.spring.restaurant.vo;

import kr.spring.tour.vo.TourVO;
import lombok.Data;
@Data
public class RestaurantVO extends TourVO{
    private String chkCreditCardFood;
    private String discountInfoFood;
    private String firstMenu;
    private String infoCenterFood;
    private String kidsFacility;
    private String openDateFood;
    private String openTimeFood;
    private String packing;
    private String parkingFood;
    private String reservationFood;
    private String restDateFood;
    private String scaleFood;
    private Integer seat;
    private String smoking;
    private String treatMenu;
    private String lcnsNo;
}
