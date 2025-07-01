package kr.spring.auth.security;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Slf4j
@Component("customFailureHandler")
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
		
		String username = request.getParameter("username");
		String errorMessage = getErrorMessage(exception);
		
		log.warn("로그인 실패: 사용자 = {}, 원인 = {}", username, exception.getMessage());
		
		// 에러 메시지를 URL 인코딩
		String encodedErrorMessage = URLEncoder.encode(errorMessage, StandardCharsets.UTF_8);
		
		// 로그인 페이지로 리다이렉트 (에러 메시지 포함)
		String failureUrl = "/member/login?error=true&exception=" + encodedErrorMessage;
		
		setDefaultFailureUrl(failureUrl);
		
		super.onAuthenticationFailure(
				     request, response, exception);	
	}
	
	/**
	 * 예외 타입에 따른 에러 메시지 반환
	 */
	private String getErrorMessage(AuthenticationException exception) {
		if (exception instanceof BadCredentialsException) {
			return "아이디 또는 비밀번호가 올바르지 않습니다.";
		} else if (exception instanceof UsernameNotFoundException) {
			return "존재하지 않는 사용자입니다.";
		} else if (exception instanceof CredentialsExpiredException) {
			return "비밀번호가 만료되었습니다.";
		} else if (exception instanceof LockedException) {
			return "계정이 잠겨있습니다.";
		} else if (exception instanceof DisabledException) {
			return "계정이 비활성화되었습니다.";
		} else {
			return "로그인에 실패했습니다. 다시 시도해주세요.";
		}
	}
}








