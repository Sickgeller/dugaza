package kr.spring.common;

import lombok.Getter;

@Getter
public enum RoleType {
    ADMIN("ADMIN"),
    MEMBER("MEMBER"),
    INACTIVE("INACTIVE"),
    SUSPENDED("SUSPENDED"),
    SELLER("SELLER");

    private final String value;

    RoleType(String value) {
        this.value = value;
    }
}
