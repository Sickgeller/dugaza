package kr.spring.api.dto;

import lombok.Data;

@Data
public class KakaoUserInfoDto {
    private Long id;
    private String email;
    private String nickname;
    private String profileImage;
    private String thumbnailImage;
    private String name;
    private String phoneNumber;
    private String birthday;
    private String gender;
} 