package kr.spring.community.vo;

import java.util.Date;
import lombok.Data;

@Data
public class CommunityReplyVO {
	private int isModified; // 0 또는 1
    private Long replyId;
    private Long postId;
    private Long memberId; 
    private String content;
    private Date regdate;
    private String writer;

}
