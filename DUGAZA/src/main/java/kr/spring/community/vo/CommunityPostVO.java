package kr.spring.community.vo;

import java.util.Date;

import lombok.Data;

@Data
public class CommunityPostVO {
    private Long id;
    private String category;       // review / plan / mate / qna
    private String title;
    private String content;
    private String writer;
    private String filename;       // 업로드된 파일명
    private Date regdate;
    private int viewCount;
    private int likeCount;
    private int commentCount;
}
