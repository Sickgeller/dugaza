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
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        
        // 사용자 정보 로깅
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        log.info("로그인 성공: 사용자 = {}, 권한 = {} 아이디 = {} 사용자? = {}", 
                userDetails.getUsername(), userDetails.getAuthorities(), userDetails.getUserId(), userDetails.getMember());
        
        // 요청한 사용자 타입 확인
        String requestedUserType = request.getParameter("userType");
        log.info("요청된 사용자 타입: {}", requestedUserType);
        
        // 사용자 타입과 실제 권한 검증
        if (!validateUserTypeAndRole(requestedUserType, userDetails)) {
            log.warn("사용자 타입 불일치: 요청타입 = {}, 실제권한 = {}", 
                    requestedUserType, userDetails.getAuthorities());
            
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
            return "/admin";
        } else {
            return "/";
        }
    }
} 