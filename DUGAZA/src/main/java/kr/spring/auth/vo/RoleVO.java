package kr.spring.auth.vo;

import lombok.Data;
import java.util.List;

@Data
public class RoleVO {
    private Long roleId;
    private String roleName;
    private String roleDesc;
    private String isExpression;
    
    // 역할에 연결된 리소스들
    private List<ResourceVO> resources;
    
    // 이 역할을 가진 사용자들
    private List<Long> accountIds;
} 