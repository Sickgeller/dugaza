package kr.spring.auth.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;


import kr.spring.auth.security.CustomAccessDeniedHandler;
import kr.spring.auth.security.CustomLogoutSuccessHandler;
import kr.spring.auth.security.UserDetailsService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    
    @Value("${data-config.rememberMe-key}")
    private String rememberMeKey;

    @Autowired
    @Qualifier("dataSource")
    private DataSource dataSource;
    
    @Autowired
    private UserDetailsService userDetailsService;
    
    @Autowired
    @Qualifier("customSuccessHandler")
    private AuthenticationSuccessHandler successHandler;
    
    @Autowired
    @Qualifier("customFailureHandler")
    private AuthenticationFailureHandler failureHandler;
    
    @Autowired
    private CustomAccessDeniedHandler customAccessDeniedHandler;
    
    @Autowired
    @Qualifier("customLogoutSuccessHandler")
    private CustomLogoutSuccessHandler logoutSuccessHandler;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) 
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * 웹 애플리케이션용 Security Filter Chain
     */
    @Bean
    @Order(2)
    public SecurityFilterChain webSecurityFilterChain(HttpSecurity http) throws Exception {
        
        return http
                .securityMatcher("/**")
                .authorizeHttpRequests(authorize -> authorize
                    .requestMatchers("/css/**", "/js/**", "/images/**", "/assets/**", "/favicon.*").permitAll() // 정적 리소스 허용
                    .requestMatchers("/", "/member/login", "/member/register", "/member/registerUser").permitAll() // 인증 없이 접근 가능한 페이지
                    .requestMatchers("/views/common/**").permitAll() // 공통 페이지들
                    .requestMatchers("/transportation/**").permitAll() // 교통 관련 페이지 및 API 허용
                    .requestMatchers("/seller/login", "/seller/register").permitAll() // 판매자 로그인/가입 페이지
                    .requestMatchers("/seller/**").hasAnyRole("SELLER", "CAR", "HOUSE") // 판매자 전용 페이지
                    .requestMatchers("/admin/**").hasRole("ADMIN")// 관리자 전용 페이지
                    // API 제외한 나머지 요청은 인증 필요
                    .requestMatchers("/api/**").denyAll() // API는 별도 필터체인에서 처리
                    .anyRequest().authenticated()
                )
                .formLogin(form -> form
                    .loginPage("/member/login")
                    .loginProcessingUrl("/auth/login")
                    .usernameParameter("username")
                    .passwordParameter("password")
                    .successHandler(successHandler)
                    .failureHandler(failureHandler)
                    .permitAll()
                )
                .logout(logout -> logout
                    .logoutUrl("/member/logout")  // 로그아웃 처리 URL
                    .logoutSuccessHandler(logoutSuccessHandler)  // 커스텀 로그아웃 성공 핸들러
                    .invalidateHttpSession(true)  // 세션 무효화
                    .clearAuthentication(true)    // 인증 정보 클리어
                    .deleteCookies("JSESSIONID", "remember-me")  // 쿠키 삭제
                    .permitAll()  // 로그아웃 URL에 모든 사용자 접근 허용
                )
                .exceptionHandling(exception -> exception
                    .accessDeniedHandler(customAccessDeniedHandler)
                )
                .rememberMe(remember -> remember
                    .key(rememberMeKey)
                    .tokenRepository(persistentTokenRepository())
                    .tokenValiditySeconds(60 * 60 * 24 * 7) // 7일
                    .userDetailsService(userDetailsService)
                    .useSecureCookie(false) // HTTPS가 아닌 환경에서도 쿠키 사용 가능
                    .rememberMeParameter("remember-me") // 파라미터 이름 명시
                    .rememberMeCookieName("remember-me") // 쿠키 이름 명시
                    .alwaysRemember(false) // 체크박스 선택 시에만 remember-me 활성화
                )
                .authenticationProvider(authenticationProvider()) // 명시적 AuthenticationProvider 설정
                .csrf(csrf -> csrf
                    .ignoringRequestMatchers("/transportation/bus/search") // 고속버스 검색 API는 CSRF 비활성화
                )
                .build();
    }

    /**
     * REST API용 Security Filter Chain 역할별로
     * API부분은 따로 시작부분 수정해야함
     */
//    @Bean
//    @Order(1)
//    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
//
//        return http
//                .securityMatcher("/api/**")
//                .authorizeHttpRequests(authorize -> authorize
//                    .requestMatchers("/api/public/**").permitAll()
//                    .requestMatchers("/api/user/**").hasRole("USER")
//                    .requestMatchers("/api/seller/**").hasRole("SELLER")
//                    .requestMatchers("/api/admin/**").hasRole("ADMIN")
//                    .anyRequest().authenticated()
//                )
//                .authenticationProvider(authenticationProvider()) // 명시적 AuthenticationProvider 설정
//                .csrf(csrf -> csrf.disable())
//                .httpBasic(basic -> {}) // REST API는 Basic Auth 사용
//                .build();
//    } // 개발하는동안은 모두허용

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * DaoAuthenticationProvider 설정
     * 순환참조를 방지하면서 사용자 인증 서비스 사용
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
        tokenRepository.setDataSource(dataSource);
        return tokenRepository;
    }
} 