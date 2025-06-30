package kr.spring.auth.security;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.FlashMapManager;
import org.springframework.web.servlet.support.SessionFlashMapManager;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
//로그인 실패 시 처리를 담당하는 클래스.
//사용자가 인증(로그인)을 시도했지만 실패했을 때,
//사용자를 어떤 URL로 리다이렉트할지 지정하거나 추가적인 로직 실행
public class CustomFailureHandler 
          extends SimpleUrlAuthenticationFailureHandler{
	@Override
	public void onAuthenticationFailure(
			               HttpServletRequest request,
			               HttpServletResponse response,
			          AuthenticationException exception)
	              throws IOException,ServletException{
		log.debug("[Spring Security Login Check 2] AuthenticationFailureHandler 실행");
		log.debug("[Spring Security Login Check 2] 로그인 실패 : " + exception.toString());
		
		final FlashMap flashMap = new FlashMap();
		flashMap.put("error", "error");
		final FlashMapManager flashMapManager = 
				new SessionFlashMapManager();
		flashMapManager.saveOutputFlashMap(
				       flashMap, request, response);
		
		setDefaultFailureUrl("/member/login");
		
		super.onAuthenticationFailure(
				     request, response, exception);	
	}
}








