package kr.spring.auth.security;

import kr.spring.member.vo.MemberVO;
import kr.spring.member.vo.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class CustomOAuth2User implements OAuth2User {

    private final MemberVO member;
    private final Map<String, Object> attributes;

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Member의 role 처리 (MEMBER, ADMIN)
        UserRole userRole = UserRole.fromValue(member.getRole());
        return List.of(new SimpleGrantedAuthority(userRole.getRoleCode()));
    }

    @Override
    public String getName() {
        return member.getName();
    }
    
    public MemberVO getMember() {
        return member;
    }
}
