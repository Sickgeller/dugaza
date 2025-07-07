package kr.spring.house.faq.vo;

import java.sql.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class FaqVO {
	private Long faqId;
	private String category;
	private String question;
	private String answer;
	private Integer orderNum;
	private String isActive;
	private Date createdAt;
	private Date updatedAt;
}
