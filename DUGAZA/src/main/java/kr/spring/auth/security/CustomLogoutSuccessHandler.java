package kr.spring.auth.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component("customLogoutSuccessHandler")
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, 
                               Authentication authentication) throws IOException, ServletException {
        
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            log.info("로그아웃 성공: 사용자 = {}, 권한 = {}", 
                    userDetails.getUsername(), userDetails.getAuthorities());
        } else {
            log.info("로그아웃 성공: 익명 사용자");
        }
        
        // 로그아웃 성공 메시지를 세션에 저장 (리다이렉트 후 표시용)
        request.getSession().setAttribute("logoutMessage", "정상적으로 로그아웃되었습니다.");
        
        // 홈페이지로 리다이렉트
        response.sendRedirect(request.getContextPath() + "/?logout=success");
    }
} 