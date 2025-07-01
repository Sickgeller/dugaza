package kr.spring.auth.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component("customSuccessHandler")
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    
    private final RequestCache requestCache = new HttpSessionRequestCache();
    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, 
                                      Authentication authentication) throws IOException, ServletException {
        
        // 사용자 정보 로깅
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        log.info("로그인 성공: 사용자 = {}, 권한 = {}", 
                userDetails.getUsername(), userDetails.getAuthorities());
        
        // Remember-me 체크 여부 로깅
        String rememberMeParam = request.getParameter("remember-me");
        log.info("Remember-me 파라미터: {}", rememberMeParam);
        
        // 세션에 사용자 정보 저장
        HttpSession session = request.getSession();
        session.setAttribute("user", userDetails);
        
        // 기본 리다이렉트 URL 설정
        String defaultUrl = determineDefaultUrl(userDetails);
        setDefaultTargetUrl(defaultUrl);
        
        // Remember-me 처리를 위해 부모 클래스의 메서드 호출
        super.onAuthenticationSuccess(request, response, authentication);
    }
    
    /**
     * 사용자 역할에 따른 기본 리다이렉트 URL 결정
     */
    private String determineDefaultUrl(CustomUserDetails userDetails) {
        if (userDetails.isSeller()) {
            return "/seller/dashboard";
        } else if (userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
            return "/admin";
        } else {
            return "/";
        }
    }
} 