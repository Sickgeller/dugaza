package kr.spring.auth.vo;

import lombok.Data;
import java.util.List;

@Data
public class ResourceVO {
    private Long resourceId;
    private String resourceName;
    private String httpMethod;
    private int orderNum;
    private String resourceType;
    
    // 이 리소스에 접근 가능한 역할들
    private List<RoleVO> roles;
} 