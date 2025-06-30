package kr.spring.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExpressBusGradeApiDto {
    private Long gradeId; // 고속버스 등급 식별자
    private String gradeNm; // EX) 우등 고속 심야고속...
}
