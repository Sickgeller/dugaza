package kr.spring.seller.vo;

import lombok.Builder;
import lombok.Data;

import java.sql.Date;
import java.util.Objects;

@Data
@Builder
public class SellerVO {
    private Long sellerId;
    private String id;          // 기존 id -> loginId
    private String password;
    private String name;
    private String email;
    private String license;
    private String sellerType;
    private String status;
    private String address;
    private String addressDetail;
    private String phone;            // 기본 전화번호
    private String secondaryPhone;   // 보조 전화번호 (기존 tell)
    private String role;
    private Date createdAt;
    private Date updatedAt;

}

/**

 CREATE TABLE SELLER(
    SELLER_ID NUMBER PRIMARY KEY,
    ID VARCHAR2(50) UNIQUE,
    PASSWORD VARCHAR2(300),
    NAME VARCHAR2(20),
    EMAIL VARCHAR2(200),
    PHONE VARCHAR2(50),
    ADDRESS VARCHAR2(200),
    ADDRESS_DETAIL VARCHAR(200),
    TELL VARCHAR2(200),
    ROLE
 )

 */
