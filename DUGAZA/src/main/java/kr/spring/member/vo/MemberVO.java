package kr.spring.member.vo;

import java.sql.Date;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

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






