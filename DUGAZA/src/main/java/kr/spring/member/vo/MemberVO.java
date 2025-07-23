package kr.spring.member.vo;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class MemberVO {

	private Long memberId;
	@NotNull
	@Pattern(regexp = "^[a-z0-9]{4,12}$" , message = "아이디는 4~12자, 소문자와 숫자로 구성되어야합니다.")
	private String loginId;
	@NotNull
	@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*])[A-Za-z\\d!@#$%^&*]{8,16}$", message = "비밀번호는 소문자,대문자,숫자로 이루어진 10~20자")
	private String password;
	@NotNull
	@Email
	private String email;
	@NotNull
	private String name;
	@NotNull
	@Pattern(regexp = "^01[016789]-\\d{3,4}-\\d{4}$")
	private String phone;
	@NotNull(message = "주소를 입력해주세요")
	private String address;
	private String addressDetail;
	@NotNull(message = "성별을 입력해주세요")
	private String gender;
	private String profileImageUrl;
	private int status;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private int role;

	private Long kakaoId;


}

/*
CREATE TABLE MEMBER(
    MEMBER_ID NUMBER PRIMARY KEY,
    ID VARCHAR2(50) UNIQUE NOT NULL,
    PASSWORD VARCHAR2(300),
    NAME VARCHAR2(20),
    EMAIL VARCHAR2(200),
    PHONE VARCHAR2(50),
    ADDRESS VARCHAR2(200),
    ADDRESS_DETAIL VARCHAR2(200),
    STATUS VARCHAR2(20),
    ROLE VARCHAR2(20),
    CREATED_AT DATE,
    UPDATED_AT DATE,
    PROFILE_IMAGE VARCHAR2(300)
);

 */






