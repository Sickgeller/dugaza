package kr.spring.auth.vo;

import lombok.Data;

@Data
public class KakaoOauth2AccessTokenVO {
    private String iss; //ID 토큰을 발급한 인증 기관 정보 https://kauth.kakao.com으로 고정
    private String aud; // ID 토큰이 발급된 앱의 앱 키 인가 코드 받기 요청 시 client_id에 전달된 앱 키 Kakao SDK를 사용한 카카오 로그인의 경우, 해당 SDK 초기화 시 사용된 앱 키
    private String sub; // ID 토큰에 해당하는 사용자의 회원번호
    private Integer iat; // ID 토큰 발급 또는 갱신 시각, UNIX 타임스탬프(Timestamp)
    private Integer exp; // ID 토큰 만료 시간, UNIX 타임스탬프(Timestamp)
    private String nonce; // 사용자가 카카오 로그인으로 인증을 완료한 시각, UNIX 타임스탬프(Timestamp)
    private String nickname; // 인가 코드 받기 요청 시 전달한 nonce 값과 동일한 값
    private String picture; // 프로필 미리보기 이미지 URL
    private String email; // 	카카오계정 대표 이메일
}
