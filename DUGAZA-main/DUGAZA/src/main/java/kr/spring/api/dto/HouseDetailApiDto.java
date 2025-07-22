package kr.spring.api.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class HouseDetailApiDto {
    private Long contentId;
    private Long contentTypeId; // 32 고정
    private Long accomCountLodging;
    private String checkInTime;
    private String checkOutTime;
    private String chkCooking;
    private String foodPlace;
    private String infoCenterLodging;
    private String parkingLodging;
    private String pickup;
    private int roomCount;
    private String reservationLodging;
    private String reservationUrl;
    private String roomType;
    private String scaleLodging;
    private String subFacility;
    private String barbecue;
    private String beauty;
    private String beverage;
    private String bicycle;
    private String campfire;
    private String fitness;
    private String karaoke;
    private String publicBath;
    private String publicPC;
    private String sauna;
    private String seminar;
    private String sports;
    private String refundRegulation;
}
