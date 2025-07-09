package kr.spring.community.vo;

import java.util.Date;
import lombok.Data;

@Data
public class CommunityReplyVO {
    private Long replyId;
    private Long postId;
    private Long memberId; 
    private String content;
    private Date regdate;
}
