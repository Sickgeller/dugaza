package kr.spring.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import kr.spring.member.service.MemberService;
import kr.spring.member.vo.MemberVO;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/auth")
@CrossOrigin
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private MemberService memberService;

    @PostMapping("/member-login")
    public ResponseEntity<?> authenticateUser(@RequestBody MemberVO loginRequest, HttpSession session) {
        try {
            // 인증 객체 생성
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getId(),
                    loginRequest.getPassword()
                )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            MemberVO member = memberService.selectCheckMember(loginRequest.getId());

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "로그인 성공");
            response.put("memberId", member.getMemberId());
            response.put("name", member.getName());

            log.debug("<<로그인 성공>> memberId: {}", member.getMemberId());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("<<로그인 실패>> error: {}", e.getMessage());
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "아이디 또는 비밀번호가 일치하지 않습니다.");
            
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/check")
    public ResponseEntity<?> checkLoginStatus() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.isAuthenticated() && 
            !authentication.getPrincipal().equals("anonymousUser")) {
            
            MemberVO member = memberService.selectCheckMember(authentication.getName());
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "authenticated");
            response.put("memberId", member.getMemberId());
            response.put("name", member.getName());
            
            return ResponseEntity.ok(response);
        }
        
        return ResponseEntity.ok(Map.of("status", "not_authenticated"));
    }
}
