<details> <summary># 🔐 DUGAZA Spring Security 시스템</summary>

> **"복잡한 보안을 간단하게, 강력한 인증을 유연하게"**

## 🎯 프로젝트 개요

DUGAZA는 여행 정보 통합 플랫폼으로, **다중 사용자 타입**과 **소셜 로그인**을 지원하는 고도화된 Spring Security 시스템을 구축했습니다.

### 🌟 핵심 특징
- **다중 사용자 타입**: Member(일반회원) + Seller(판매자) 분리 관리
- **소셜 로그인**: 카카오 OAuth2 완전 통합
- **다중 Security Filter Chain**: 웹과 API 보안 정책 분리
- **동적 권한 관리**: 역할 기반 접근 제어 (RBAC)
- **Remember-Me**: 7일간 자동 로그인

---

## 🏗️ 아키텍처 구조

### 📊 전체 보안 구조도
```
┌─────────────────────────────────────────────────────────────┐
│                    DUGAZA Security System                   │
├─────────────────────────────────────────────────────────────┤
│  🌐 Web Filter Chain (@Order(2))                           │
│  ├── Form Login (Member/Seller)                            │
│  ├── OAuth2 Login (Kakao)                                  │
│  ├── Remember-Me (7일)                                     │
│  └── Role-based Access Control                             │
├─────────────────────────────────────────────────────────────┤
│  🔌 API Filter Chain (@Order(1))                           │
│  ├── Basic Authentication                                  │
│  └── API-specific Authorization                            │
├─────────────────────────────────────────────────────────────┤
│  👥 User Management                                        │
│  ├── CustomUserDetails (Member + Seller)                   │
│  ├── CustomUserDetailsService                              │
│  └── CustomOAuth2UserService                               │
└─────────────────────────────────────────────────────────────┘
```

---

## 🔧 핵심 컴포넌트
<details>
<summary>### 1. 🎭 다중 Security Filter Chain</summary>

#### **웹 애플리케이션용 Filter Chain**
```java
@Bean
@Order(2)
public SecurityFilterChain webSecurityFilterChain(HttpSecurity http) {
    return http
        .securityMatcher("/**")
        .authorizeHttpRequests(authorize -> authorize
            // 정적 리소스 허용
            .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
            // 공개 페이지
            .requestMatchers("/", "/member/login", "/member/register").permitAll()
            // 역할별 접근 제어
            .requestMatchers("/seller/**").hasAnyRole("SELLER", "CAR", "HOUSE")
            .requestMatchers("/admin/**").hasRole("ADMIN")
            // API는 별도 처리
            .requestMatchers("/api/**").denyAll()
            .anyRequest().authenticated()
        )
        .formLogin(form -> form
            .loginPage("/member/login")
            .successHandler(successHandler)
            .failureHandler(failureHandler)
        )
        .oauth2Login(oauth2 -> oauth2
            .userInfoEndpoint(userInfo -> 
                userInfo.userService(customOAuth2UserService)
            )
        )
        .rememberMe(remember -> remember
            .key(rememberMeKey)
            .tokenValiditySeconds(60 * 60 * 24 * 7) // 7일
        )
        .build();
}
```

#### **REST API용 Filter Chain**
```java
@Bean
@Order(1)
public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) {
    return http
        .securityMatcher("/api/**")
        .authorizeHttpRequests(authorize -> authorize
            .requestMatchers("/api/public/**").permitAll()
            .requestMatchers("/api/user/**").hasRole("USER")
            .requestMatchers("/api/seller/**").hasRole("SELLER")
            .requestMatchers("/api/admin/**").hasRole("ADMIN")
            .anyRequest().authenticated()
        )
        .httpBasic(basic -> {}) // API에 적합한 인증
        .build();
}
```
</details>
<details><summary>
### 2. 👤 통합 사용자 관리 (CustomUserDetails)</summary>

#### **다중 사용자 타입 지원**
```java
public class CustomUserDetails implements UserDetails {
    private final MemberVO member;
    private SellerVO seller;
    
    // 생성자 오버로딩으로 다양한 사용자 타입 지원
    public CustomUserDetails(MemberVO member) { ... }
    public CustomUserDetails(MemberVO member, SellerVO seller) { ... }
    public CustomUserDetails(SellerVO seller) { ... }
    
    // 동적 권한 부여
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        
        // Member 권한 (MEMBER, ADMIN)
        if (member != null) {
            UserRole userRole = UserRole.fromValue(member.getRole());
            authorities.add(new SimpleGrantedAuthority(userRole.getRoleCode()));
        }
        
        // Seller 권한 (SELLER, CAR_SELLER, HOUSE_SELLER)
        if (seller != null) {
            SellerRole sellerRole = SellerRole.valueOf(seller.getRole());
            authorities.add(new SimpleGrantedAuthority("ROLE_" + sellerRole.getValue()));
        }
        
        return authorities;
    }
}
```
</details>
<details><summary>
### 3. 🔗 OAuth2 소셜 로그인 (카카오)</summary>

#### **카카오 로그인 플로우**
```java
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    
    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) {
        OAuth2User user = super.loadUser(request);
        String registrationId = request.getClientRegistration().getRegistrationId();
        
        if ("kakao".equals(registrationId)) {
            // 카카오 API 응답 처리
            Long providerId = Long.valueOf(user.getAttribute("id").toString());
            
            // 1. 기존 회원 조회
            MemberVO member = findByKakaoId(providerId);
            
            if (member == null) {
                // 2. 신규 회원 → 통합 페이지로 리다이렉트
                throw new BadCredentialsException("NEW_ACCOUNT_REQUIRED:" + providerId);
            }
            
            if ("KAKAO".equals(member.getStatus())) {
                // 3. 미완료 카카오 회원 → 통합 페이지로 리다이렉트
                throw new BadCredentialsException("INTEGRATION_REQUIRED:" + providerId);
            }
            
            return new CustomOAuth2User(member, user.getAttributes());
        }
    }
}
```

#### **카카오 계정 통합 플로우**
```
1. 카카오 로그인 시도
   ↓
2. 기존 회원인가?
   ├─ Yes → 로그인 성공
   └─ No → 3번으로
   ↓
3. 신규 회원인가?
   ├─ Yes → 통합 페이지로 이동 (신규 가입)
   └─ No → 4번으로
   ↓
4. 미완료 카카오 회원인가?
   ├─ Yes → 통합 페이지로 이동 (기존 계정 연동)
   └─ No → 로그인 성공
```
</details>
<details><summary>
### 4. 🎯 인증 핸들러</summary>

#### **성공 핸들러 (역할별 리다이렉트)**
```java
@Component
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, 
                                      HttpServletResponse response, 
                                      Authentication authentication) {
        
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        
        // 사용자 타입 검증
        if (!validateUserTypeAndRole(requestedUserType, userDetails)) {
            clearAuthenticationAndSession(request, response, authentication);
            redirectToLoginPage(requestedUserType);
            return;
        }
        
        // 역할별 리다이렉트
        String targetUrl = determineTargetUrl(userDetails);
        setDefaultTargetUrl(targetUrl);
        
        super.onAuthenticationSuccess(request, response, authentication);
    }
    
    private String determineTargetUrl(CustomUserDetails userDetails) {
        if (userDetails.isSeller()) {
            return "/seller/dashboard";
        } else if (userDetails.hasRole("ROLE_ADMIN")) {
            return "/admin/dashboard";
        } else {
            return "/";
        }
    }
}
```

#### **실패 핸들러 (OAuth2 통합 처리)**
```java
@Component
public class CustomFailureHandler implements AuthenticationFailureHandler {
    
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, 
                                      HttpServletResponse response, 
                                      AuthenticationException exception) {
        
        // OAuth2 통합/가입 요청 처리
        if (exception.getMessage().startsWith("INTEGRATION_REQUIRED:")) {
            // 기존 회원 통합 페이지로 리다이렉트
            redirectToIntegrationPage(request, response, exception.getMessage());
        } else if (exception.getMessage().startsWith("NEW_ACCOUNT_REQUIRED:")) {
            // 신규 가입 페이지로 리다이렉트
            redirectToRegistrationPage(request, response, exception.getMessage());
        } else {
            // 일반 로그인 실패 처리
            redirectToLoginPageWithError(request, response, exception);
        }
    }
}
```
</details>
---

### ✅ **성과**
- **다중 사용자 타입**: Member/Seller 완벽 분리 관리
- **소셜 로그인**: 카카오 OAuth2 완전 통합
- **보안 강화**: CSRF, 세션 관리, Remember-Me
- **확장성**: 새로운 역할/권한 쉽게 추가 가능
- **유지보수성**: 명확한 책임 분리로 코드 관리 용이

---
</details>
