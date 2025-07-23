package kr.spring.auth.security;

import kr.spring.member.enums.MemberRole;
import kr.spring.member.enums.MemberStatus;
import kr.spring.member.vo.MemberVO;
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

    public CustomUserDetails(MemberVO member, SellerVO seller) {
        this.seller = seller;
        this.member = member;
    }


    // 일반 회원인지 확인
    public boolean isMember() {
        return member != null;
    }

    public boolean isSeller(){
        return member.getRole() == MemberRole.SELLER.getValue();
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

        for(MemberRole role : MemberRole.values()){
            if(role.getValue() == member.getRole()){
                authorities.add(new SimpleGrantedAuthority(role.getRole()));
            }
        }
        return authorities;
    }
    
    @Override
    public String getPassword() {
       return member.getPassword();
    }
    
    @Override
    public String getUsername() {
      return member.getLoginId();

    }
    
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    
    @Override
    public boolean isAccountNonLocked() {
        return MemberStatus.SUSPENDED.getValue() == member.getStatus();
    }
    
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    
    @Override
    public boolean isEnabled() {
        return member.getStatus() == MemberStatus.ACTIVE.getValue();
    }
    
    // 사용자 ID 반환 (Long 타입)
    public Long getUserId() {
        return member.getMemberId();
    }
} 