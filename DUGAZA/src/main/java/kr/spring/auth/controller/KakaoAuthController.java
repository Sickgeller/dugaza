package kr.spring.auth.controller;

import jakarta.servlet.http.HttpSession;
import kr.spring.api.service.KakaoLoginService;
import kr.spring.member.vo.MemberVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Slf4j
@Controller
@RequestMapping("/auth/kakao")
@RequiredArgsConstructor
public class KakaoAuthController {
    
    private final KakaoLoginService kakaoLoginService;
    
    @Value("${api.kakao.rest-api-key}")
    private String restApiKey;
    
    @Value("${api.kakao.base-url}")
    private String baseUrl;
    
    /**
     * 카카오 로그인 페이지로 리다이렉트
     */
    @GetMapping("/login")
    public String kakaoLogin(HttpSession session) {
        // CSRF 방지를 위한 state 값 생성
        String state = UUID.randomUUID().toString();
        session.setAttribute("kakao_state", state);
        
        String redirectUri = "http://localhost:8080/auth/kakao/callback";
        
        String kakaoAuthUrl = String.format(
            "%s/oauth/authorize?response_type=code&client_id=%s&redirect_uri=%s&state=%s",
            baseUrl, restApiKey, redirectUri, state
        );
        
        return "redirect:" + kakaoAuthUrl;
    }
    
    /**
     * 카카오 로그인 콜백 처리
     */
    @GetMapping("/callback")
    public String kakaoCallback(
            @RequestParam("code") String code,
            @RequestParam("state") String state,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        
        try {
            // state 값 검증
            String savedState = (String) session.getAttribute("kakao_state");
            if (savedState == null || !savedState.equals(state)) {
                redirectAttributes.addFlashAttribute("error", "잘못된 요청입니다.");
                return "redirect:/member/login";
            }
            
            // 세션에서 state 제거
            session.removeAttribute("kakao_state");
            
            String redirectUri = "http://localhost:8080/auth/kakao/callback";
            
            // 카카오 로그인 처리
            MemberVO member = kakaoLoginService.processKakaoLogin(code, redirectUri).block();
            
            if (member != null) {
                // 로그인 성공 - 세션에 사용자 정보 저장
                session.setAttribute("user", member);
                session.setAttribute("memberId", member.getMemberId());
                session.setAttribute("memberName", member.getName());
                session.setAttribute("memberRole", member.getRole());
                
                log.info("카카오 로그인 성공: {}", member.getEmail());
                
                // 계정 통합 여부 확인 (카카오 ID가 있으면 통합된 계정)
                if (member.getKakaoId() != null) {
                    redirectAttributes.addFlashAttribute("message", "카카오 로그인에 성공했습니다.");
                } else {
                    redirectAttributes.addFlashAttribute("message", "기존 계정과 카카오 계정이 연결되었습니다.");
                }
                
                return "redirect:/";
            } else {
                redirectAttributes.addFlashAttribute("error", "로그인 처리 중 오류가 발생했습니다.");
                return "redirect:/member/login";
            }
            
        } catch (Exception e) {
            log.error("카카오 로그인 처리 중 오류 발생", e);
            redirectAttributes.addFlashAttribute("error", "로그인 처리 중 오류가 발생했습니다.");
            return "redirect:/member/login";
        }
    }
}
