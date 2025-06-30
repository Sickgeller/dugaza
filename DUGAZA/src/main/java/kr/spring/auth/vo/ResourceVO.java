package kr.spring.auth.vo;

import lombok.Data;
import java.util.List;

@Data
public class ResourceVO {
    private Long resourceId; // 리소스 고유 식별자
    private String resourceName; // URL패턴, 리소스 경로
    private String httpMethod; // HTTP 메서드 (GET, POST, PUT, DELETE 등)
    private int orderNum; // 정렬 순서 (낮을수록 먼저 적용) 
    private String resourceType; // 리소스 타입 (WEB, API, STATIC)
    
    // 이 리소스에 접근 가능한 역할들
    private List<RoleVO> roles;
} 

/*
    
시스템상에서 접근 제어가 필요한 모든 URL 패턴과 API 엔드포인트 기술술
 
 */