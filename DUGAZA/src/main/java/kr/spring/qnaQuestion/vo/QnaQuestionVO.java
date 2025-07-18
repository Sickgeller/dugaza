package kr.spring.qnaQuestion.vo;

import java.sql.Date;

import kr.spring.qnaResponse.vo.QnaResponseVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class QnaQuestionVO {
	
	private QnaResponseVO response;

	public QnaResponseVO getResponse() {
	    return response;
	}
	public void setResponse(QnaResponseVO response) {
	    this.response = response;
	}
	
    private Long qna_id;
    private String category;
    private String title;
    private String content;
    private Long member_id;
    private Date created_at;
    private String is_answered;
}
