package kr.spring.api.dto;

import lombok.Builder;
import lombok.Data;



@Data
@Builder
public class TouristAttrationDetailApiDto {
    private Long contentId;
    private Long contentTypeId; // 12 고정
    private Long accomCount;
    private String chkBabyCarriage;
    private String chkCreditCard;
    private String chkPet;
    private String expAgeRange;
    private String expGuide;
    private Long heritage1;
    private Long heritage2;
    private Long heritage3;
    private String infoCenter;
    private String openDate;
    private String parking;
    private String restDate;
    private String useSeason;
    private String useTime;
}
