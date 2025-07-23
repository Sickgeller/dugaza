package kr.spring.member.enums;

import lombok.Getter;

@Getter
public enum MemberStatus {

    ACTIVE("ACTIVE", 0),
    INACTIVE("INACTIVE", 1),
    SUSPENDED("SUSPENDED", 2),
    DELETED("DELETED", 3);

    private final String status;
    private final int value;

    MemberStatus(String status, int value) {
        this.status = status;
        this.value = value;
    }
}
