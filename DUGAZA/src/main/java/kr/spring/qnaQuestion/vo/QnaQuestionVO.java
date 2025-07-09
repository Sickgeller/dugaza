package kr.spring.qnaQuestion.vo;

import java.sql.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class QnaQuestionVO {
    private Long questionId;
    private String category;
    private String title;
    private String content;
    private Long memberId;
    private Date createdAt;
}
