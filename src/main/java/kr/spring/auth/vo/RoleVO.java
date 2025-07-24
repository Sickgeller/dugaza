package kr.spring.auth.vo;

import lombok.Data;

import java.util.List;

@Data
public class RoleVO {
    private Long roleId; // 역할 고유 식별자
    private String roleName; // 내부 코드용 역할명
    // private String roleCode; // Spring Security용 역할코드  위랑 통일해서 쓸지 아니면 따로쓸지는 상의
    private String roleDesc;  // 역할 상세 설명 (시스템 관리자 권한, 멤버권한, 판매자 권한...)
    private String isExpression; // SpEl 역할 표현식 사용 여부 (Y/N)
    private String displayName; // 표시용 역할명 (관리자, 멤버, 판매자)
    // 역할에 연결된 리소스들
    private List<ResourceVO> resources;
    
    // 이 역할을 가진 사용자들
    private List<Long> accountIds;
} 

/*
  시스템에서 사용할 모든 역할을 정의하는 마스터 테이블
 */