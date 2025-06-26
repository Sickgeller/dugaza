package kr.spring.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 모든 서비스 메서드에 대한 로깅을 담당하는 AOP 어드바이스
 */
@Aspect
@Component
@Slf4j
public class ServiceLoggingAspect {

    /**
     * 모든 서비스 메서드에 대한 포인트컷
     */
    @Pointcut("execution(* kr.spring..*.service..*Service*.*(..))")
    private void allServiceMethods() {}

    /**
     * @LogExecutionTime 어노테이션이 없는 메서드에 대한 포인트컷
     */
    @Pointcut("allServiceMethods() && !@annotation(kr.spring.aop.LogExecutionTime)")
    private void nonAnnotatedServiceMethods() {}

    /**
     * 모든 서비스 메서드 실행 전후 로깅
     */
    @Around("nonAnnotatedServiceMethods()")
    public Object logServiceMethodExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String category = "[" + className.replace("ServiceImpl", "").replace("Service", "") + "]";
        
        // 메서드 파라미터 로깅
        Object[] args = joinPoint.getArgs();
        String params = Arrays.stream(args)
                .map(arg -> arg != null ? arg.toString() : "null")
                .collect(Collectors.joining(", "));
        
        log.debug("{} 서비스 시작 - 메서드: {}, 파라미터: [{}]", 
                category, methodName, params);
        
        LocalDateTime startTime = LocalDateTime.now();
        Object result;
        
        try {
            // 메서드 실행
            result = joinPoint.proceed();
            
            long executionTime = ChronoUnit.MILLIS.between(startTime, LocalDateTime.now());
            
            // 결과 요약
            String resultSummary = summarizeResult(result);
            log.debug("{} 서비스 완료 - 메서드: {}, 실행 시간: {}ms, 결과: {}", 
                    category, methodName, executionTime, resultSummary);
            
            return result;
        } catch (Exception e) {
            long executionTime = ChronoUnit.MILLIS.between(startTime, LocalDateTime.now());
            log.error("{} 서비스 오류 - 메서드: {}, 실행 시간: {}ms, 오류: {}", 
                    category, methodName, executionTime, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * 결과를 요약하여 문자열로 반환
     */
    private String summarizeResult(Object result) {
        if (result == null) {
            return "null";
        }
        
        if (result instanceof Map<?, ?>) {
            return result.toString();
        }
        
        if (result instanceof List<?>) {
            List<?> list = (List<?>) result;
            return String.format("리스트 (크기: %d)", list.size());
        }
        
        // 기본 타입이나 문자열인 경우 그대로 반환
        if (result instanceof String || result instanceof Number || result instanceof Boolean) {
            return result.toString();
        }
        
        // 그 외의 경우 클래스 이름만 반환
        return result.getClass().getSimpleName() + " 객체";
    }
} 