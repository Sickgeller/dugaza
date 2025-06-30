package kr.spring.member.security;

import kr.spring.member.vo.MemberVO;
import kr.spring.seller.vo.SellerVO;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class PrincipalDetails implements UserDetails {
    
    private MemberVO member;
    private SellerVO seller;
    
    // 일반 회원용 생성자
    public PrincipalDetails(MemberVO member) {
        this.member = member;
    }
    
    // 판매자용 생성자
    public PrincipalDetails(SellerVO seller) {
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
        if (isSeller()) {
            return Collections.singletonList(new SimpleGrantedAuthority("ROLE_SELLER"));
        } else if (isMember()) {
            String role = member.getRole();
            if ("ADMIN".equals(role)) {
                return Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"));
            } else {
                return Collections.singletonList(new SimpleGrantedAuthority("ROLE_MEMBER"));
            }
        }
        return Collections.emptyList();
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
} 