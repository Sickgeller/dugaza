package kr.spring.seller.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.Objects;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SellerVO {
    private Long memberId;
    private String businessName;
    private String license;
    private Long sellerType;
    private String businessAddress;
    private String businessAddressDetail;
    private String businessPhone;
    private String businessSecondaryPhone;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

/**
 * SELLER 테이블 구조:
 * SELLER_ID NUMBER(19,0) PRIMARY KEY
 * ID VARCHAR2(50 BYTE) UNIQUE
 * PASSWORD VARCHAR2(100 BYTE)
 * NAME VARCHAR2(100 BYTE) - 대표자 이름
 * BUSINESS_NAME VARCHAR2(100 BYTE) - 상호명
 * EMAIL VARCHAR2(100 BYTE)
 * LICENSE VARCHAR2(50 BYTE)
 * SELLER_TYPE VARCHAR2(50 BYTE)
 * STATUS VARCHAR2(20 BYTE)
 * ADDRESS VARCHAR2(255 BYTE)
 * ADDRESS_DETAIL VARCHAR2(255 BYTE)
 * PHONE VARCHAR2(20 BYTE)
 * SECONDARY_PHONE VARCHAR2(20 BYTE)
 * ROLE VARCHAR2(50 BYTE)
 * CREATED_AT DATE
 * UPDATED_AT DATE
 */
