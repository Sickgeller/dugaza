package kr.spring.auth.controller;

import jakarta.servlet.http.HttpSession;
import kr.spring.api.dto.KakaoUserInfoDto;
import kr.spring.api.service.KakaoLoginService;
import kr.spring.member.service.MemberService;
import kr.spring.member.vo.MemberVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
    private final MemberService memberService;
    
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
            
        } catch (RuntimeException e) {
            // 통합 확인이 필요한 경우
            if (e.getMessage() != null && e.getMessage().startsWith("INTEGRATION_REQUIRED:")) {
                String[] parts = e.getMessage().split(":");
                if (parts.length >= 3) {
                    String email = parts[1];
                    String kakaoId = parts[2];
                    return "redirect:/auth/kakao/integration?email=" + email + "&kakaoId=" + kakaoId;
                }
            }
            
            log.error("카카오 로그인 처리 중 오류 발생", e);
            redirectAttributes.addFlashAttribute("error", "로그인 처리 중 오류가 발생했습니다.");
            return "redirect:/member/login";
            
        } catch (Exception e) {
            log.error("카카오 로그인 처리 중 오류 발생", e);
            redirectAttributes.addFlashAttribute("error", "로그인 처리 중 오류가 발생했습니다.");
            return "redirect:/member/login";
        }
    }
    
    /**
     * 계정 통합 확인 페이지 표시
     */
    @GetMapping("/integration")
    public String showIntegrationPage(
            @RequestParam("email") String email,
            @RequestParam("kakaoId") Long kakaoId,
            Model model) {
        
        try {
            // 기존 회원 정보 조회
            MemberVO existingMember = memberService.findByEmail(email);
            if (existingMember == null) {
                return "redirect:/member/login?error=invalid_email";
            }
            
            // 카카오 사용자 정보 조회 (세션에서 가져오거나 다시 조회)
            KakaoUserInfoDto kakaoUserInfo = new KakaoUserInfoDto();
            kakaoUserInfo.setId(kakaoId);
            kakaoUserInfo.setEmail(email);
            // 추가 카카오 정보는 필요시 세션에서 가져오거나 API 재호출
            
            model.addAttribute("existingMember", existingMember);
            model.addAttribute("kakaoUserInfo", kakaoUserInfo);
            
            return "member/kakao-integration";
            
        } catch (Exception e) {
            log.error("계정 통합 페이지 표시 중 오류 발생", e);
            return "redirect:/member/login?error=integration_error";
        }
    }
    
    /**
     * 계정 통합 처리
     */
    @GetMapping("/integrate")
    public String processIntegration(
            @RequestParam("confirm") boolean confirm,
            @RequestParam(value = "kakaoId", required = false) Long kakaoId,
            @RequestParam(value = "email", required = false) String email,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        
        try {
            if (!confirm) {
                // 통합 취소 - 로그인 페이지로 리다이렉트
                redirectAttributes.addFlashAttribute("message", "정 통합이 취소되었습니다.");
                return "redirect:/member/login";
            }
            
            if (kakaoId == null || email == null) {
                redirectAttributes.addFlashAttribute("error", "잘못된 요청입니다.");
                return "redirect:/member/login";
            }
            
            // 기존 회원 조회
            MemberVO existingMember = memberService.findByEmail(email);
            if (existingMember == null) {
                redirectAttributes.addFlashAttribute("error", "존재하지 않는 회원입니다.");
                return "redirect:/member/login";
            }
            
            // 카카오 ID만 연결 (다른 정보는 변경하지 않음)
            memberService.linkKakaoAccount(existingMember.getMemberId(), kakaoId);
            
            // 로그인 처리
            session.setAttribute("user", existingMember);
            session.setAttribute("memberId", existingMember.getMemberId());
            session.setAttribute("memberName", existingMember.getName());
            session.setAttribute("memberRole", existingMember.getRole());
            
            log.info("카카오 계정 통합 완료: {} -> {}", kakaoId, email);
            
            redirectAttributes.addFlashAttribute("message", "카카오 계정이 성공적으로 연결되었습니다.");
            return "redirect:/";
            
        } catch (Exception e) {
            log.error("계정 통합 처리 중 오류 발생", e);
            redirectAttributes.addFlashAttribute("error", "계정통합 처리 중 오류가 발생했습니다.");
            return "redirect:/member/login";
        }
    }
}
