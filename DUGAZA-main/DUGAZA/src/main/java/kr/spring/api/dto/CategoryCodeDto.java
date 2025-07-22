package kr.spring.api.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CategoryCodeDto {
    private String categoryCode;  // PK: 카테고리 코드 값
    private String categoryName;  // 카테고리 이름
    private String parentCode;    // FK: 상위 카테고리 코드 (null이면 최상위)
    private Integer codeLevel;    // 1=대분류, 2=중분류, 3=소분류
    private Long contentTypeId;     //콘텐츠 타입 id
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
