package kr.spring.auth.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component("customSuccessHandler")
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final RequestCache requestCache = new HttpSessionRequestCache();
    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        
        // 사용자 정보 로깅 - OAuth2와 일반 로그인 모두 처리
        Object principal = authentication.getPrincipal();
        log.info("로그인 성공: 사용자 타입 = {}", principal.getClass().getSimpleName());
        
        if (principal instanceof CustomOAuth2User) {
            // OAuth2 로그인 처리
            handleOAuth2Login(request, response, authentication, (CustomOAuth2User) principal);
        } else if (principal instanceof CustomUserDetails) {
            // 일반 로그인 처리
            handleNormalLogin(request, response, authentication, (CustomUserDetails) principal);
        } else {
            log.warn("알 수 없는 사용자 타입: {}", principal.getClass().getName());
            super.onAuthenticationSuccess(request, response, authentication);
        }
    }
    
    private void handleOAuth2Login(HttpServletRequest request, HttpServletResponse response, Authentication authentication, CustomOAuth2User oauth2User) throws IOException, ServletException {
        log.info("OAuth2 로그인 성공: 사용자 = {}, 권한 = {}", 
                oauth2User.getName(), oauth2User.getAuthorities());
        
        // 세션에 사용자 정보 저장
        HttpSession session = request.getSession();
        session.setAttribute("user", oauth2User);
        session.setAttribute("member", oauth2User.getMember());
        
        // 기본 리다이렉트 URL 설정 (OAuth2는 일반적으로 홈으로)
        setDefaultTargetUrl("/");
        
        // 부모 클래스의 메서드 호출
        super.onAuthenticationSuccess(request, response, authentication);
    }
    
    private void handleNormalLogin(HttpServletRequest request, HttpServletResponse response, Authentication authentication, CustomUserDetails userDetails) throws IOException, ServletException {
        log.info("일반 로그인 성공: 사용자 = {}, 권한 = {} 아이디 = {} 사용자? = {}", 
                userDetails.getUsername(), userDetails.getAuthorities(), userDetails.getUserId(), userDetails.getMember());
        
        // 요청한 사용자 타입 확인
        String requestedUserType = request.getParameter("userType");
        log.info("요청된 사용자 타입: {}", requestedUserType);
        
        // 사용자 타입과 실제 권한 검증
        if (!validateUserTypeAndRole(requestedUserType, userDetails)) {
            log.warn("사용자 타입 불일치: 요청타입 = {}, 실제권한 = {}", 
                    requestedUserType, userDetails.getAuthorities());
            
            // 세션 정리 및 SecurityContext 클리어
            clearAuthenticationAndSession(request, response, authentication);
            
            // 적절한 로그인 페이지로 리다이렉트 (에러 메시지와 함께)
            String redirectUrl = getFailureRedirectUrl(requestedUserType);
            redirectStrategy.sendRedirect(request, response, redirectUrl);
            return;
        }
        
        // Remember-me 체크 여부 로깅
        String rememberMeParam = request.getParameter("remember-me");
        log.info("Remember-me 파라미터: {}", rememberMeParam);
        
        // 세션에 사용자 정보 저장
        HttpSession session = request.getSession();
        session.setAttribute("user", userDetails);

        if(userDetails.isSeller()) {
            session.setAttribute("seller", userDetails.getSeller());
        }else if(userDetails.isMember()) {
            session.setAttribute("member", userDetails.getMember());
        }
        
        // 기본 리다이렉트 URL 설정
        String defaultUrl = determineDefaultUrl(userDetails);
        setDefaultTargetUrl(defaultUrl);
        
        // Remember-me 처리를 위해 부모 클래스의 메서드 호출
        super.onAuthenticationSuccess(request, response, authentication);
    }
    
    /**
     * 인증 정보와 세션을 정리하는 메서드
     */
    private void clearAuthenticationAndSession(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        // SecurityContext 클리어
        SecurityContextHolder.clearContext();
        
        // 세션에서 인증 관련 정보 제거
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute("user");
            session.removeAttribute("seller");
            session.removeAttribute("member");
            session.removeAttribute("SPRING_SECURITY_CONTEXT");
        }
        
        // SecurityContextLogoutHandler를 사용하여 완전한 로그아웃 처리
        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(request, response, authentication);
    }
    
    /**
     * 요청된 사용자 타입과 실제 사용자 권한이 일치하는지 검증
     */
    private boolean validateUserTypeAndRole(String requestedUserType, CustomUserDetails userDetails) {
        if (requestedUserType == null) {
            // userType이 없으면 기본적으로 허용 (하위 호환성)
            return true;
        }
        
        switch (requestedUserType.toLowerCase()) {
            case "member":
                // 일반 회원은 isMember()가 true이고 isSeller()가 false여야 함
                return userDetails.isMember() && !userDetails.isSeller();
                        
            case "seller":
                // 판매자는 isSeller()가 true여야 함
                return userDetails.isSeller();
                
            default:
                log.warn("알 수 없는 사용자 타입: {}", requestedUserType);
                return false;
        }
    }
    
    /**
     * 검증 실패 시 리다이렉트할 URL 결정
     */
    private String getFailureRedirectUrl(String requestedUserType) {
        if ("seller".equals(requestedUserType)) {
            return "/seller/login?error=true&reason=invalid_user_type";
        } else {
            return "/member/login?error=true&reason=invalid_user_type";
        }
    }
    
    /**
     * 사용자 역할에 따른 기본 리다이렉트 URL 결정
     */
    private String determineDefaultUrl(CustomUserDetails userDetails) {
        if (userDetails.isSeller()) {
            return "/seller/dashboard";
        } else if (userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
            return "/member/admin_member"; // 관리자는 회원관리 페이지로 리다이렉트
        } else {
            return "/";
        }
    }
} 