package kr.spring.auth.security;

import kr.spring.member.enums.MemberRole;
import kr.spring.member.vo.MemberVO;
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
        int role = member.getRole();
        for(MemberRole roles : MemberRole.values()){
            if(roles.getValue() == role){
                return List.of(new SimpleGrantedAuthority(roles.getRole()));
            }
        }
        return null;
    }

    @Override
    public String getName() {
        return member.getName();
    }
}
