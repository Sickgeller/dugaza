package kr.spring.qnaQuestion.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import static org.springframework.security.config.Customizer.withDefaults;

import kr.spring.qnaQuestion.service.CustomUserDetailsService;

@Configuration
public class QnaSecurityConfig {

    @Bean(name = "qnaUserDetailsService")
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailsService();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/faq/**").authenticated()
                .anyRequest().permitAll()
            )
            .formLogin(withDefaults()) // 기본 로그인 폼 사용
            .csrf(csrf -> csrf.disable()); // CSRF 비활성화

        return http.build();
    }
}







