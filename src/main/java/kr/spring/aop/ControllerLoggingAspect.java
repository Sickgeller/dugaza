package kr.spring.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * 모든 컨트롤러 메서드에 대한 로깅을 담당하는 AOP 어드바이스
 */
@Aspect
@Component
@Slf4j
public class ControllerLoggingAspect {

    /**
     * 모든 컨트롤러 메서드에 대한 포인트컷 (@Controller 및 @RestController)
     */
    @Pointcut("execution(* kr.spring..*.controller..*Controller.*(..))")
    private void allControllerMethods() {}
    
    /**
     * @RestController 어노테이션이 붙은 클래스의 모든 메서드
     */
    @Pointcut("@within(org.springframework.web.bind.annotation.RestController)")
    private void allRestControllerMethods() {}
    
    /**
     * @Controller 어노테이션이 붙은 클래스의 모든 메서드  
     */
    @Pointcut("@within(org.springframework.stereotype.Controller)")
    private void allControllerAnnotatedMethods() {}
    
    /**
     * 모든 컨트롤러 메서드 (패턴 + 어노테이션 기반)
     */
    @Pointcut("allControllerMethods() || allRestControllerMethods() || allControllerAnnotatedMethods()")
    private void allWebControllerMethods() {}

    /**
     * 모든 컨트롤러 메서드 실행 전후 로깅
     */
    @Around("allWebControllerMethods()")
    public Object logControllerMethodExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String category = "[" + className.replace("Controller", "") + "]";
        
        // REST API인지 확인
        boolean isRestController = joinPoint.getTarget().getClass()
                .isAnnotationPresent(org.springframework.web.bind.annotation.RestController.class);
        
        HttpServletRequest request = null;
        String requestURI = "";
        String httpMethod = "";
        
        // 요청 정보 가져오기 시도
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                request = attributes.getRequest();
                requestURI = request.getRequestURI();
                httpMethod = request.getMethod();
            }
        } catch (Exception e) {
            // 요청 정보를 가져오는 데 실패해도 로깅은 계속 진행
            log.debug("요청 정보를 가져오는 데 실패했습니다: {}", e.getMessage());
        }
        
        // 메서드 파라미터 로깅
        Object[] args = joinPoint.getArgs();
        String params = Arrays.stream(args)
                .map(arg -> arg != null ? arg.toString() : "null")
                .collect(Collectors.joining(", "));
        
        String controllerType = isRestController ? "REST API" : "WEB";
        log.info("{} [{}] 요청 시작 - URI: {} [{}], 메서드: {}, 파라미터: [{}]", 
                category, controllerType, requestURI, httpMethod, methodName, params);
        
        LocalDateTime startTime = LocalDateTime.now();
        Object result;
        
        try {
            // 메서드 실행
            result = joinPoint.proceed();
            
            long executionTime = ChronoUnit.MILLIS.between(startTime, LocalDateTime.now());
            log.info("{} [{}] 요청 완료 - URI: {} [{}], 메서드: {}, 실행 시간: {}ms", 
                    category, controllerType, requestURI, httpMethod, methodName, executionTime);
            
            return result;
        } catch (Exception e) {
            long executionTime = ChronoUnit.MILLIS.between(startTime, LocalDateTime.now());
            log.error("{} [{}] 요청 오류 - URI: {} [{}], 메서드: {}, 실행 시간: {}ms, 오류: {}", 
                    category, controllerType, requestURI, httpMethod, methodName, executionTime, e.getMessage(), e);
            throw e;
        }
    }
} 