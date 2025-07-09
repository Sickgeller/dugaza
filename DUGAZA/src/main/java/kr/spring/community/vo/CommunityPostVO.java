package kr.spring.community.vo;

import java.util.Date;

import lombok.Data;

@Data
public class CommunityPostVO {
    private Long id;
    private String category;       // review / plan / mate / qna
    private String title;
    private String content;
    private Long memberId;       // 작성자 (member_id로 매핑)
    private String writer; 			//작성자 아이디
    private String filename;       // 업로드된 파일명
    private Date regdate;
    private int viewCount;
    private int likeCount;
    private int commentCount;
}
