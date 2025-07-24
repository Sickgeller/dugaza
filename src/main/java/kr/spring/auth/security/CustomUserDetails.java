package kr.spring.auth.security;

import kr.spring.member.vo.MemberVO;
import kr.spring.member.vo.UserRole;
import kr.spring.seller.vo.SellerRole;
import kr.spring.seller.vo.SellerVO;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {
    
    private final MemberVO member;
    private SellerVO seller;
    
    // 일반 회원용 생성자
    public CustomUserDetails(MemberVO member) {
        this.member = member;
    }

    // 판매자 회원용 생성자 (Member가 있는 경우)
    public CustomUserDetails(MemberVO member, SellerVO seller) {
        this.seller = seller;
        this.member = member;
    }
    
    // Seller만 있는 경우 생성자
    public CustomUserDetails(SellerVO seller) {
        this.seller = seller;
        this.member = null;
    }

    // 일반 회원인지 확인
    public boolean isMember() {
        return member != null;
    }

    // 판매자인지 확인
    public boolean isSeller(){
        return seller != null;
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

        // Member가 있는 경우 Member의 role 처리 (MEMBER, ADMIN)
        if (member != null) {
            UserRole userRole = UserRole.fromValue(member.getRole());
            authorities.add(new SimpleGrantedAuthority(userRole.getRoleCode()));
        }
        
        // Seller가 있는 경우 Seller의 role 추가
        if (seller != null) {
            SellerRole sellerRole = SellerRole.valueOf(seller.getRole());
            authorities.add(new SimpleGrantedAuthority("ROLE_" + sellerRole.getValue()));
        }
        
        return authorities;
    }
    
    @Override
    public String getPassword() {
        if (member != null) {
            return member.getPassword();
        } else if (seller != null) {
            return seller.getPassword();
        }
        return null;
    }
    
    @Override
    public String getUsername() {
        if (member != null) {
            return member.getId();
        } else if (seller != null) {
            return seller.getId();
        }
        return null;
    }
    
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    
    @Override
    public boolean isAccountNonLocked() {
        if (member != null) {
            // Member의 경우 "SUSPEND" 상태가 아닌 경우 계정 잠금 해제
            return !"SUSPEND".equals(member.getStatus());
        } else if (seller != null) {
            // Seller의 경우 status가 "ACTIVE"가 아닌 경우 정지된 것으로 간주
            return "ACTIVE".equals(seller.getStatus());
        }
        return true;
    }
    
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    
    @Override
    public boolean isEnabled() {
        if (member != null) {
            // Member의 경우 "ACTIVE" 상태인 경우만 활성
            return "ACTIVE".equals(member.getStatus());
        } else if (seller != null) {
            // Seller의 경우 status가 "ACTIVE"인 경우만 활성
            return "ACTIVE".equals(seller.getStatus());
        }
        return false;
    }
    
    // 사용자 ID 반환 (Long 타입)
    public Long getUserId() {
        if (member != null) {
            return member.getMemberId();
        } else if (seller != null) {
            return seller.getSellerId();
        }
        return null;
    }
} 