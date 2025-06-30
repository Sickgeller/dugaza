package kr.spring.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import kr.spring.auth.security.CustomAccessDeniedHandler;
import kr.spring.auth.security.UserSecurityService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
//모든 요청 URL이 스프링 시큐리티의 제어를 받도록 만드는 어노테이션
@EnableWebSecurity
//Controller 메서드 레벨에서 권한을 체크할 수 있도록 설정.
//@PreAuthorize 사용시 추가
@EnableMethodSecurity
public class SecurityConfig {
	//쿠키에 사용되는 값을 암호화하기 위한 키(key)값
	@Value("${data-config.rememberMe-key}")
	private String rememberMe_key;

	//DB연동을 위한 DataSource 지정
	@Autowired
	@Qualifier("dataSource")
	private DataSource dataSource;
	//로그인 시 사용자 정보를 조회하고, 이를 기반으로
	//인증을 수행
	@Autowired
	private UserSecurityService userSecurityService;
	//인증에 성공한 후, 리다이렉트할 URL 지정
	@Autowired
	private AuthenticationSuccessHandler authenticationSuccessHandler;
	//로그인 실패 시 처리를 담당하는 클래스
	@Autowired
	private AuthenticationFailureHandler authenticationFailureHandler;
	@Autowired
	private CustomAccessDeniedHandler customAccessDeniedHandler;

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http)
			                                              throws Exception{
		//HTTP 요청에 대한 보안 설정
		return http.authorizeHttpRequests(authorize -> authorize
//				    .requestMatchers("/admin").hasAuthority("ROLE_ADMIN")
				    .requestMatchers("/**").permitAll()
//				    .anyRequest().authenticated()
				)
			//폼 기반 로그인 설정
			.formLogin(form -> form
					//커스텀 로그인 페이지 URL 지정
					.loginPage("/member/login")
					.usernameParameter("id")
					.passwordParameter("password")
					.successHandler(authenticationSuccessHandler)
					.failureHandler(authenticationFailureHandler))
			//로그아웃 설정
			.logout(logout -> logout
					.logoutUrl("/member/logout")
					.logoutSuccessUrl("/")
					.invalidateHttpSession(true)
					.deleteCookies("JSESSIONID"))
			.exceptionHandling(error -> error
					.accessDeniedHandler(customAccessDeniedHandler)
			)
			.rememberMe(me -> me
					.key(rememberMe_key) //쿠키에 사용되는 값을 암호화하기 위한 키(key) 값
					.tokenRepository(persistentTokenRepository())//토큰은 데이터베이스에 저장
					.tokenValiditySeconds(60*60*24*7)
					.userDetailsService(userSecurityService))
			//GET방식을 제외한 상태를 변경하는 요청(POST,PUT,DELETE,
			//PATCH)에만 CSRF 검사
			.csrf(csrf -> csrf.disable()) //CSRF 보호 기능을 비활성화
			.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public PersistentTokenRepository
	                    persistentTokenRepository() {
		JdbcTokenRepositoryImpl repo =
				new JdbcTokenRepositoryImpl();
		repo.setDataSource(dataSource);
		return repo;
	}

}












