package kr.spring.auth.security;

import kr.spring.member.vo.MemberVO;
import kr.spring.seller.vo.SellerVO;
import kr.spring.auth.vo.RoleVO;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.ArrayList;
import java.util.List;

public class CustomUserDetails implements UserDetails {
    
    private MemberVO member;
    private SellerVO seller;
    
    // 일반 회원용 생성자
    public CustomUserDetails(MemberVO member) {
        this.member = member;
    }
    
    // 판매자용 생성자
    public CustomUserDetails(SellerVO seller) {
        this.seller = seller;
    }
    
    // 판매자인지 확인
    public boolean isSeller() {
        return seller != null;
    }
    
    // 일반 회원인지 확인
    public boolean isMember() {
        return member != null;
    }
    
    // 판매자 정보 반환
    public SellerVO getSeller() {
        return seller;
    }
    
    // 회원 정보 반환
    public MemberVO getMember() {
        return member;
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        
        if (isSeller()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_SELLER"));
        } else if (isMember()) {
            // 기본 역할 추가
            String basicRole = member.getRole();
            if ("ADMIN".equals(basicRole)) {
                authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            } else {
                authorities.add(new SimpleGrantedAuthority("ROLE_MEMBER"));
            }
            
            // 추가 역할들 처리 (다중 역할 지원)
            if (member.getUserRoles() != null) {
                for (RoleVO role : member.getUserRoles()) {
                    authorities.add(new SimpleGrantedAuthority(role.getRoleName()));
                }
            }
        }
        
        return authorities;
    }
    
    @Override
    public String getPassword() {
        if (isSeller()) {
            return seller.getPassword();
        } else if (isMember()) {
            return member.getPassword();
        }
        return null;
    }
    
    @Override
    public String getUsername() {
        if (isSeller()) {
            return seller.getId();
        } else if (isMember()) {
            return member.getId();
        }
        return null;
    }
    
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    
    @Override
    public boolean isAccountNonLocked() {
        if (isSeller()) {
            return !"SUSPENDED".equals(seller.getStatus());
        } else if (isMember()) {
            return !"SUSPENDED".equals(member.getStatus());
        }
        return true;
    }
    
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    
    @Override
    public boolean isEnabled() {
        if (isSeller()) {
            return "ACTIVE".equals(seller.getStatus());
        } else if (isMember()) {
            return "ACTIVE".equals(member.getStatus());
        }
        return false;
    }
    
    // 사용자 ID 반환 (Long 타입)
    public Long getUserId() {
        if (isSeller()) {
            return seller.getSellerId();
        } else if (isMember()) {
            return member.getMemberId();
        }
        return null;
    }
    
    // 사용자 이름 반환
    public String getRealName() {
        if (isSeller()) {
            return seller.getName();
        } else if (isMember()) {
            return member.getName();
        }
        return null;
    }
} 