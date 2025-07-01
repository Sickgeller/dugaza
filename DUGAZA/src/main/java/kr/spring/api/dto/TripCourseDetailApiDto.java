package kr.spring.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TripCourseDetailApiDto {
    private Long contentId;
    private Long contentTypeId; // 25 고정
    private String distance;
    private String infoCenterTourCourse;
    private String schedule;
    private String takeTime;
    private String theme;
}
