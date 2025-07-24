package kr.spring.member.vo;

import java.sql.Date;
import java.util.List;

import jakarta.validation.constraints.Pattern;
import lombok.Data;
import kr.spring.auth.vo.RoleVO;

@Data
public class MemberVO {
	private Long memberId;
	@Pattern(regexp="^[A-Za-z0-9]{4,12}$")
	private String id;
	private String password;
	private String name;
	private String email;
	private String phone;
	private String address;
	private String addressDetail;
	private String status;
	private String role;
	private Date createdAt;
	private Date updatedAt;
	private String profileImage;
	
	// 카카오 로그인 관련 필드
	private Long kakaoId; // 카카오 고유 ID (NULL이면 이메일 가입자, 있으면 카카오 가입자)
	private String nickname; // 닉네임
	
	// 사용자가 가진 역할들 (다중 역할 지원)
	private List<RoleVO> userRoles;
	
	// 역할 문자로 출력
	public String getStringStatus() {
		switch (this.status) {
		case "ACTIVE": return "일반";
		case "SUSPEND": return "정지";
		case "HUMAN": return "휴먼";
		case "WITHDRAWN": return "탈퇴";
		default: return "오류";
		}
	}
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






