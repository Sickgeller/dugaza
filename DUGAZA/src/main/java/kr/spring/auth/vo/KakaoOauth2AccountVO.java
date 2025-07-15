package kr.spring.auth.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class KakaoOauth2AccountVO {
    /** 프로필 정보 제공 동의 여부 */
    private Boolean profileNeedsAgreement;

    /** 닉네임 제공 동의 여부 */
    private Boolean profileNicknameNeedsAgreement;

    /** 프로필 이미지 제공 동의 여부 */
    private Boolean profileImageNeedsAgreement;

    /** 프로필 정보 (닉네임, 프로필 이미지 등) */
    private Profile profile;

    /** 카카오 계정 이름 제공 동의 여부 */
    private Boolean nameNeedsAgreement;

    /** 카카오 계정 이름 */
    private String name;

    /** 이메일 제공 동의 여부 */
    private Boolean emailNeedsAgreement;

    /** 이메일 유효 여부 (true: 유효함) */
    private Boolean isEmailValid;

    /** 이메일 인증 여부 (true: 인증됨) */
    private Boolean isEmailVerified;

    /** 카카오 계정 이메일 */
    private String email;

    /** 연령대 제공 동의 여부 */
    private Boolean ageRangeNeedsAgreement;

    /** 연령대 (예: "20~29") */
    private String ageRange;

    /** 출생 연도 제공 동의 여부 */
    private Boolean birthyearNeedsAgreement;

    /** 출생 연도 (YYYY 형식) */
    private String birthyear;

    /** 생일 제공 동의 여부 */
    private Boolean birthdayNeedsAgreement;

    /** 생일 (MMDD 형식) */
    private String birthday;

    /** 생일 타입 (SOLAR: 양력, LUNAR: 음력) */
    private String birthdayType;

    /** 윤달 여부 (true: 윤달 생일) */
    private Boolean isLeapMonth;

    /** 성별 제공 동의 여부 */
    private Boolean genderNeedsAgreement;

    /** 성별 (male: 남성, female: 여성) */
    private String gender;

    /** 전화번호 제공 동의 여부 */
    private Boolean phoneNumberNeedsAgreement;

    /** 전화번호 (국내는 +82 00-0000-0000 형식) */
    private String phoneNumber;

    /** CI(연계정보) 제공 동의 여부 */
    private Boolean ciNeedsAgreement;

    /** 연계정보(CI) */
    private String ci;

    /** CI 발급 시각 (UTC 기준) */
    private LocalDateTime ciAuthenticatedAt;

    /**
     * 프로필 정보 VO
     */
    @Data
    public static class Profile {
        /** 닉네임 */
        private String nickname;

        /** 프로필 이미지 URL */
        private String profileImageUrl;

        /** 썸네일 이미지 URL */
        private String thumbnailImageUrl;
    }
}
