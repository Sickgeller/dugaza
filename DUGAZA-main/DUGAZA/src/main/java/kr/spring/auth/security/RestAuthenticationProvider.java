package kr.spring.auth.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
//@Component  // 순환참조 방지를 위해 비활성화 - DaoAuthenticationProvider 사용
@RequiredArgsConstructor
public class RestAuthenticationProvider implements AuthenticationProvider {
    
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        
        String username = authentication.getName();
        String password = (String) authentication.getCredentials();
        
        log.debug("REST API 인증 시도: {}", username);
        
        // 사용자 정보 로드
        CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(username);
        
        // 비밀번호 검증
        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            log.warn("REST API 인증 실패 - 잘못된 비밀번호: {}", username);
            throw new BadCredentialsException("잘못된 비밀번호입니다.");
        }
        
        // 계정 상태 검증
        if (!userDetails.isEnabled()) {
            log.warn("REST API 인증 실패 - 비활성화된 계정: {}", username);
            throw new BadCredentialsException("비활성화된 계정입니다.");
        }
        
        if (!userDetails.isAccountNonLocked()) {
            log.warn("REST API 인증 실패 - 잠긴 계정: {}", username);
            throw new BadCredentialsException("잠긴 계정입니다.");
        }
        
        log.info("REST API 인증 성공: {}", username);
        
        return new UsernamePasswordAuthenticationToken(
            userDetails, 
            null, 
            userDetails.getAuthorities()
        );
    }
    
    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
} 