package kr.spring.member.vo;

import lombok.Getter;

@Getter
public enum UserRole {
	// 일반 회원 역할
	MEMBER("MEMBER", "ROLE_MEMBER", "일반 회원"),
	ADMIN("ADMIN", "ROLE_ADMIN", "관리자");
	
	private final String value;        // DB에 저장되는 값
	private final String roleCode;     // Spring Security에서 사용할 권한 코드
	private final String displayName;  // 화면에 표시될 이름
	
	UserRole(String value, String roleCode, String displayName) {
		this.value = value;
		this.roleCode = roleCode;
		this.displayName = displayName;
	}
	
	// value로 enum 찾기
	public static UserRole fromValue(String value) {
		for (UserRole role : values()) {
			if (role.value.equals(value)) {
				return role;
			}
		}
		return MEMBER; // 기본값
	}
}
