package kr.spring.config;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import kr.spring.websocket.SocketHandler;

// 자바코드 기반 설정 클래스
@Configuration
@EnableWebSecurity
public class AppConfig implements WebMvcConfigurer, WebSocketConfigurer{

	@Value("${dataconfig.google-mail-url}")
	private String google_mail_url;
	@Value("${dataconfig.google-mail-password}")
	private String google_mail_password;

	
	/*
	 * 1. 구글 이메일 접속
	 * 2. 계정관리
	 * 3. 보안
	 * 4. 2단계 인증
	 * 5. 앱 비밀번호 (표시가 되지 않을 경우 6번 수행)
	 * 6. 구글에서 "구글 2단계 인증 앱 비밀번호" 검색
	 * 7. 앱 비밀번호로 로그인 - Google 계정 고객센터 클릭
	 * 8. 앱 비밀번호를 만들고 관리합니다 클릭
	 * */
	// 웹소켓 셋팅
	@Bean
	public JavaMailSenderImpl javaMailSenderImpl() {
		Properties prop = new Properties();
		prop.put("mail.smtp.ssl.trust", "smtp.gmail.com");
		prop.put("mail.smtp.starttls.enable", "true");
		prop.put("mail.transport.protocol", "smtp");
		prop.put("mail.smtp.auth", "true");
		prop.put("mail.debug", "true");
		
		JavaMailSenderImpl javaMail = new JavaMailSenderImpl();
		javaMail.setHost("smtp.gmail.com");
		javaMail.setPort(587);
		javaMail.setDefaultEncoding("utf-8");
		javaMail.setUsername(google_mail_url);
		javaMail.setPassword(google_mail_password);
		javaMail.setJavaMailProperties(prop);
		
		return javaMail;
	}
	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
																	// 허용할 도메인 지정
		registry.addHandler(new SocketHandler(), "message-ws").setAllowedOrigins("*");
		
	}

}
