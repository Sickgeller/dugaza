package kr.spring.qnaResponse.vo;

import java.sql.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class QnaResponseVO {
    private Long response_id;
    private Long qna_id;
    private Long member_id;
    private String content;
    private Date created_at;

    // Getter, Setter
}

