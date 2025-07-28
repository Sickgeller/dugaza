# 🔐 DUGAZA 인증/인가 시스템 (Spring Security & OAuth2)

---

## 🎯 프로젝트 개요

DUGAZA는 다양한 사용자 유형(회원/판매자/관리자)과 소셜 로그인(OAuth2, 카카오/구글 등), 역할 기반 인가(RBAC), 세션/쿠키/RememberMe, API/웹 분리 보안정책을 모두 지원하는 Spring Security 기반 인증/인가 시스템을 구축하였습니다.

---

##  아키텍처 구조

###  전체 인증/인가 시스템 구조도
```
┌───────────────────────────────────────────────┐
│                DUGAZA Security System        │
├───────────────────────────────────────────────┤
│  🌐 Web Filter Chain (@Order(2))             │
│   ├─ Form Login (회원/판매자)                │
│   ├─ OAuth2 Login (카카오/구글)              │
│   ├─ Remember-Me (7일)                       │
│   └─ Role-based Access Control               │
├───────────────────────────────────────────────┤
│  🔒 API Filter Chain (@Order(1))             │
│   ├─ Basic Authentication                    │
│   └─ API-specific Authorization              │
├───────────────────────────────────────────────┤
│  👥 User Management                          │
│   ├─ CustomUserDetails (회원/판매자)         │
│   ├─ CustomUserDetailsService                │
│   └─ CustomOAuth2UserService                 │
└───────────────────────────────────────────────┘
```

---

## 🛠️ 핵심 컴포넌트

### 1. SecurityConfig (설정)
- Web/Api FilterChain 분리, 인가 정책, 폼로그인, OAuth2, RememberMe, CSRF 등 전체 보안 정책 관리

### 2. CustomUserDetailsService
- 일반 로그인(회원/판매자) 시 DB에서 사용자 정보 로딩, 역할 부여

### 3. CustomOAuth2UserService
- OAuth2(카카오/구글) 로그인 시 providerId/email로 회원 조회, 신규/통합 처리

### 4. CustomOAuth2User
- OAuth2 인증 후 사용자 정보 객체(권한 포함)

### 5. CustomSuccessHandler/CustomFailureHandler
- 로그인/소셜 로그인 성공/실패 후 세션 저장, 리다이렉트, 예외처리 등 담당

---

## 🔄 인증/인가 흐름

### 1. 폼 로그인(회원/판매자)
1. `/member/login` 또는 `/seller/login`에서 로그인 폼 제출
2. CustomUserDetailsService에서 userType(회원/판매자) 구분, DB에서 사용자 조회
3. 인증 성공 시 CustomSuccessHandler에서 세션에 사용자 정보 저장, 역할별 리다이렉트
4. 인증 실패 시 CustomFailureHandler에서 에러 메시지 처리 및 리다이렉트

### 2. OAuth2 소셜 로그인(카카오/구글)
1. `/oauth2/authorization/{provider}` 진입 → 외부 인증서버 리다이렉트
2. 인증 후 콜백 → CustomOAuth2UserService에서 providerId/email 등으로 회원 조회
   - 신규 회원: 예외 발생 → `/member/kakao/integration` 등으로 리다이렉트(통합/가입)
   - 기존 회원: CustomOAuth2User 생성, 인증 성공 처리
3. 인증 성공 시 CustomSuccessHandler에서 세션 저장, 홈 등으로 리다이렉트
4. 인증 실패/통합 필요 시 CustomFailureHandler에서 예외 메시지 파싱, 통합/가입 페이지로 리다이렉트

---

## 🧩 주요 설정 및 코드 예시

### 1. Web Filter Chain
```java
@Bean
@Order(2)
public SecurityFilterChain webSecurityFilterChain(HttpSecurity http) {
    return http
        .securityMatcher("/**")
        .authorizeHttpRequests(authorize -> authorize
            .requestMatchers("/css/**", "/js/**", "/images/**", "/assets/**").permitAll()
            .requestMatchers("/", "/member/login", "/member/register").permitAll()
            .requestMatchers("/seller/**").hasAnyRole("SELLER", "CAR", "HOUSE")
            .requestMatchers("/admin/**").hasRole("ADMIN")
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
            .successHandler(successHandler)
            .failureHandler(failureHandler)
        )
        .rememberMe(remember -> remember
            .key(rememberMeKey)
            .tokenValiditySeconds(60 * 60 * 24 * 7)
        )
        .csrf(csrf -> csrf
            .ignoringRequestMatchers("/transportation/bus/search", "/api/**", "/admin/**")
        )
        .build();
}
```

### 2. API Filter Chain
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
        .httpBasic(basic -> {})
        .build();
}
```

### 3. CustomUserDetails (회원/판매자 동적 권한)
```java
public class CustomUserDetails implements UserDetails {
    private MemberVO member;
    private SellerVO seller;
    // ...
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (member != null) {
            UserRole userRole = UserRole.fromValue(member.getRole());
            authorities.add(new SimpleGrantedAuthority(userRole.getRoleCode()));
        }
        if (seller != null) {
            SellerRole sellerRole = SellerRole.valueOf(seller.getRole());
            authorities.add(new SimpleGrantedAuthority("ROLE_" + sellerRole.getValue()));
        }
        return authorities;
    }
}
```

### 4. OAuth2 소셜 로그인 (카카오/구글)
```java
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) {
        OAuth2User user = super.loadUser(request);
        String registrationId = request.getClientRegistration().getRegistrationId();
        if ("kakao".equals(registrationId)) {
            Long providerId = Long.valueOf(user.getAttribute("id").toString());
            MemberVO member = findByKakaoId(providerId);
            if (member == null) {
                throw new BadCredentialsException("NEW_ACCOUNT_REQUIRED:" + providerId);
            }
            if ("KAKAO".equals(member.getStatus())) {
                throw new BadCredentialsException("INTEGRATION_REQUIRED:" + providerId);
            }
            return new CustomOAuth2User(member, user.getAttributes());
        }
        // ...
    }
}
```

---

## 🔍 OAuth2 인가 상세 흐름

1. 소셜 로그인 버튼 클릭 → `/oauth2/authorization/kakao` 등 진입
2. 외부 인증서버 인증 후 콜백
3. CustomOAuth2UserService.loadUser()에서 providerId/email로 회원 조회
   - 신규: 예외 발생 → 가입/통합 페이지로 리다이렉트
   - 통합 필요: 예외 발생 → 통합 페이지로 리다이렉트
   - 기존: CustomOAuth2User 생성, 인증 성공
4. 성공 시 CustomSuccessHandler에서 세션 저장, 홈 등으로 이동
5. 실패/통합 필요 시 CustomFailureHandler에서 예외 메시지 파싱, 통합/가입 페이지로 이동

---

## 🛡️ 보안 정책 및 인가 매트릭스

| 리소스 | 일반회원 | 판매자 | 관리자 | 비로그인 |
|--------|----------|--------|--------|----------|
| `/` | ✅ | ✅ | ✅ | ✅ |
| `/member/**` | ✅ | ✅ | ✅ | ❌ |
| `/seller/**` | ❌ | ✅ | ✅ | ❌ |
| `/admin/**` | ❌ | ❌ | ✅ | ❌ |
| `/api/public/**` | ✅ | ✅ | ✅ | ✅ |
| `/api/user/**` | ✅ | ✅ | ✅ | ❌ |
| `/api/seller/**` | ❌ | ✅ | ✅ | ❌ |
| `/api/admin/**` | ❌ | ❌ | ✅ | ❌ |

- CSRF: 일부 API/관리자 경로 제외
- RememberMe: 7일, DB 기반 토큰 저장
- 세션/쿠키: 로그아웃 시 무효화, 쿠키 삭제

---

## 🧩 확장/커스텀 포인트

- CustomUserDetailsService: 회원/판매자/관리자 등 다양한 사용자 유형 지원
- CustomOAuth2UserService: provider별 사용자 정보 매핑, 신규/통합 처리
- CustomSuccessHandler/FailureHandler: 로그인 후 세션/리다이렉트/에러처리 커스텀
- SecurityConfig: 인가 정책, 필터체인, RememberMe, CSRF 등 세부 정책 조정
- JWT, 추가 OAuth2 Provider, 실시간 권한변경 등 확장 가능

---

## 📝 예시 시나리오

- 카카오 신규 회원: 카카오 로그인 → DB에 KAKAO_ID 없음 → 가입 필요 예외 발생 → `/member/kakao/integration` 리다이렉트
- 카카오 기존 회원: 카카오 로그인 → DB에 KAKAO_ID 있음 → 바로 로그인 성공, 세션 저장
- 권한 없는 접근: `/admin/**`에 일반 회원 접근 → AccessDeniedHandler에서 접근 거부 처리

---

## 🏁 결론

DUGAZA의 Spring Security 시스템은 복잡한 보안 요구사항을 간단하고 유연하게 해결합니다.
- 다양한 사용자 유형 완벽 지원
- 소셜 로그인(OAuth2) 완전 통합
- 강력한 보안(세션, CSRF, RememberMe)
- 손쉬운 확장성
---