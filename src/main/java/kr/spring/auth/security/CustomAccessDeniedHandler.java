package kr.spring.auth.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.csrf.InvalidCsrfTokenException;
import org.springframework.security.web.csrf.MissingCsrfTokenException;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.FlashMapManager;
import org.springframework.web.servlet.support.SessionFlashMapManager;

import java.io.IOException;

@Slf4j
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler{

	//파일의 최대 업로드 사이트
	@Value("${spring.servlet.multipart.max-file-size:50MB}")
	private String max_file_size;

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
			AccessDeniedException accessDeniedException) throws IOException, ServletException {

		log.debug("<<예외 발생 페이지>> : {}", request.getRequestURI());
		log.error("<<예외 발생>> : {}", accessDeniedException.toString());
		
		//Ajax 요청 여부 체크
		if(isAjaxRequest(request)) {
			handleAjaxErrorResponse(
					request, response, accessDeniedException);
			return;
		}
		
		FlashMap flashMap = new FlashMap();
		FlashMapManager flashMapManager = 
				new SessionFlashMapManager();
		
		//파일 업로드 오류 체크
		String contentType = request.getContentType();
		String contentLength = 
				   request.getHeader("Content-Length");
		
		// max_file_size가 null이거나 비어있으면 기본값 사용
		if (max_file_size == null || max_file_size.isEmpty()) {
			max_file_size = "50MB";
		}
		
		long maxSize = Long.parseLong(
				max_file_size.substring(
						0,max_file_size.indexOf("MB")))*1024*1024;
		if(contentType!=null 
				&& contentType.startsWith("multipart/form-data")
				&& contentLength != null) {
			long size = Long.parseLong(contentLength);
			log.debug("<<파일 업로드 오류>> 파일 사이즈 : {}, 최대 업로드 파일 사이즈 : {}", size, maxSize);
			if(size > maxSize) {
				flashMap.put("max_size", max_file_size);
				flashMap.put("size", 
						String.format("%.2f", 
								(double)size/(1024*1024))+"MB");
				flashMapManager.saveOutputFlashMap(
						            flashMap, request, response);
				response.sendRedirect(
						request.getContextPath()+"/fileSizeLimit");
				return;
			}
		}
		
		if(accessDeniedException instanceof 
				          InvalidCsrfTokenException |
			accessDeniedException instanceof 
			              MissingCsrfTokenException) {
			
			if(request.getRequestURI().equals("/member/logout")) {
				response.sendRedirect("/main/main");
				return;
			}			
			
			flashMap.put("accessMsg", "CSRF TOKEN 미사용 또는 오류");
			flashMapManager.saveOutputFlashMap(
					         flashMap, request, response);
			response.sendRedirect(
					request.getContextPath()+"/accessDenied");
			return;
		}
		response.sendRedirect(
				request.getContextPath()+"/accessDenied");
	}
	
	//Ajax 요청 여부 확인
	private boolean isAjaxRequest(
			               HttpServletRequest request) {
		//Ajax 요청 판별 -> 
		//X-Requested-With : XMLHttpRequest 헤더 체크
		String requestedWith = 
				  request.getHeader("X-Requested-With");
		return "XMLHttpRequest".equalsIgnoreCase(requestedWith);
	}
	
	private void handleAjaxErrorResponse(
			   HttpServletRequest request,
			   HttpServletResponse response,
			   Exception ex) throws IOException{
		//인증 여부 확인
		boolean isAuthenticated = 
				request.getUserPrincipal() != null;
		
		response.setContentType(
				 "application/json;charset=UTF-8");
		
		String errorMessage;
		if(ex.toString().contains("null")) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);//403
			errorMessage = "{\"result\":\"error\",\"message\":\"CSRF 토큰 오류\"}";
		}else if(isAuthenticated) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);//403
			errorMessage = "{\"result\":\"error\",\"message\":\"권한이 없습니다.\"}";
		}else {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);//401
			errorMessage = "{\"result\":\"error\",\"message\":\"로그인 후 사용하세요(접속이 제한됨)\"}";
		}
		
		response.getWriter().write(errorMessage);		
	}
}









