package kr.spring.member.enums;

import lombok.Getter;

@Getter
public enum MemberRole {

    MEMBER("ROLE_MEMBER", 0),
    ADMIN("ROLE_ADMIN", 1),
    SELLER("ROLE_SELLER", 2);

    private final String role;
    private final int value;

    MemberRole(String role, int value) {
        this.role = role;
        this.value = value;
    }
}
