package kr.spring.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

import kr.spring.member.security.CustomAccessDeniedHandler;
import kr.spring.member.security.UserSecurityService;
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
	@Value("${dataconfig.rememberme-key}")
	private String rememberme_key;

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
	public SecurityFilterChain filterChain(HttpSecurity http)
			                                              throws Exception{
		//HTTP 요청에 대한 보안 설정
		return http.authorizeHttpRequests(authorize -> authorize
				    //롤 설정을 먼저하고 permitAll()를 호출해야
				    //정상적으로 롤이 지정됨
				    // /main/admin : ROLE_ADMIN 권한 필요 지정
//				    .requestMatchers("/main/admin").hasAuthority("ROLE_ADMIN")
				    //기타 경로: 누구나 접근 가능
				    .requestMatchers("/**").permitAll()
				    // 인증되지 않은 요청은 로그인 페이지로 리다이렉트됨
//				    .anyRequest().authenticated()
				)
			//폼 기반 로그인 설정
			.formLogin(form -> form
					//커스텀 로그인 페이지 URL 지정
					.loginPage("/member/login")
					//사용자명의 name 속성 지정
					.usernameParameter("id")
					//비밀번호의 name 속성 지정
					.passwordParameter("passwd")
					.successHandler(authenticationSuccessHandler)
					.failureHandler(authenticationFailureHandler))
			//로그아웃 설정
			.logout(logout -> logout
					//로그아웃을 처리할 URL를 지정
					.logoutUrl("/member/logout")
					//로그아웃 성공시 리다이렉트할 목적지를 지정
					.logoutSuccessUrl("/")
					//로그아웃시 세션을 무효화
					.invalidateHttpSession(true)
					//로그아웃시 쿠키를 삭제
					.deleteCookies("JSESSIONID"))
			.exceptionHandling(error -> error
					.accessDeniedHandler(customAccessDeniedHandler)
			)
			.rememberMe(me -> me
					.key(rememberme_key) //쿠키에 사용되는 값을 암호화하기 위한 키(key) 값
					.tokenRepository(persistentTokenRepository())//토큰은 데이터베이스에 저장
					.tokenValiditySeconds(60*60*24*7)
					.userDetailsService(userSecurityService))
			//CSRF(Cross-Site Request Forgery)는 인증된 사용자의
			//권한을 악용하여 사용자가 의도하지 않은 요청을 웹 서버에
			//보내게 하는 공격
			//GET방식을 제외한 상태를 변경하는 요청(POST,PUT,DELETE,
			//PATCH)에만 CSRF 검사
			//.csrf(csrf -> csrf.disable()) //CSRF 보호 기능을 비활성화
			.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	/*
	 * 자동로그인 사용시 사용하는 테이블 persistent_logins
	 * series : 사용자의 로그인 세션을 식별하는 고유한 값
	 * username : 로그인한 사용자 ID
	 * token : 사용자의 브라우저에 저장되는 토큰 값
	 * 			(쿠키에 저장되는 암호화된 토큰 값)
	 * 			이 토큰을 통해 시스템은 사용자를 인증
	 * 			매번 로그인이 유지될 때마다 갱신
	 * 			토큰이 일치하지 않으면 Remember-Me 세션이 무효화
	 * last_used : 토큰이 마지막으로 사용된 시각,
	 * 				토큰의 유효 시간을 관리하는데 사용
	 * */
	//자동로그인 처리
	@Bean
	public PersistentTokenRepository
	                    persistentTokenRepository() {
		JdbcTokenRepositoryImpl repo =
				new JdbcTokenRepositoryImpl();
		repo.setDataSource(dataSource);
		return repo;
	}

}












