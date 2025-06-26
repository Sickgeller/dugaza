package kr.spring.api.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CategoryCodeApiDto {
    private String categoryCode;  // PK: 카테고리 코드 값
    private String categoryName;  // 카테고리 이름
    private String parentCode;    // FK: 상위 카테고리 코드 (null이면 최상위)
    private Integer codeLevel;    // 1=대분류, 2=중분류, 3=소분류
    private Long contentTypeId;     //콘텐츠 타입 id
    private Long isActive;        // 활성화 상태 (1=활성화, 0=비활성화)
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
