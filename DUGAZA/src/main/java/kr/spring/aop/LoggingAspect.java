package kr.spring.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    /**
     * API 클라이언트 메서드에 대한 포인트컷
     */
    @Pointcut("execution(* kr.spring.api.client.*ApiClient.*(..))")
    private void apiClientMethods() {}

    /**
     * 서비스 구현체 메서드에 대한 포인트컷
     */
    @Pointcut("execution(* kr.spring.api.service.impl.*ServiceImpl.*(..))")
    private void serviceImplMethods() {}

    /**
     * 컨트롤러 메서드에 대한 포인트컷
     */
    @Pointcut("execution(* kr.spring.api.controller.*Controller.*(..))")
    private void controllerMethods() {}

    /**
     * @LogExecutionTime 어노테이션이 붙은 메서드에 대한 포인트컷
     */
    @Pointcut("@annotation(kr.spring.aop.LogExecutionTime)")
    private void logExecutionTimeAnnotation() {}

    /**
     * API 클라이언트 메서드 실행 전후 로깅
     */
    @Around("apiClientMethods()")
    public Object logApiClientMethodExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String category = "[" + className.replace("ApiClient", "") + "]";
        
        // 메서드 파라미터 로깅
        Object[] args = joinPoint.getArgs();
        log.info("{} API 호출 시작 - 메서드: {}, 파라미터: {}", 
                category, methodName, Arrays.toString(args));
        
        LocalDateTime startTime = LocalDateTime.now();
        Object result;
        
        try {
            // 메서드 실행
            result = joinPoint.proceed();
            
            long executionTime = ChronoUnit.MILLIS.between(startTime, LocalDateTime.now());
            
            // 결과 크기 확인
            int resultSize = getResultSize(result);
            log.info("{} API 호출 완료 - 메서드: {}, 실행 시간: {}ms, 결과 크기: {}", 
                    category, methodName, executionTime, resultSize);
            
            return result;
        } catch (Exception e) {
            log.error("{} API 호출 오류 - 메서드: {}, 오류: {}", 
                    category, methodName, e.getMessage());
            throw e;
        }
    }

    /**
     * 서비스 구현체 메서드 실행 전후 로깅
     */
    @Around("serviceImplMethods()")
    public Object logServiceMethodExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String category = "[" + className.replace("ServiceImpl", "") + "]";
        
        log.info("{} 서비스 시작 - 메서드: {}", category, methodName);
        
        LocalDateTime startTime = LocalDateTime.now();
        Object result;
        
        try {
            // 메서드 실행
            result = joinPoint.proceed();
            
            long executionTime = ChronoUnit.MILLIS.between(startTime, LocalDateTime.now());
            
            // 결과 요약
            String resultSummary = summarizeResult(result);
            log.info("{} 서비스 완료 - 메서드: {}, 실행 시간: {}ms, 결과: {}", 
                    category, methodName, executionTime, resultSummary);
            
            return result;
        } catch (Exception e) {
            log.error("{} 서비스 오류 - 메서드: {}, 오류: {}", 
                    category, methodName, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 컨트롤러 메서드 실행 전후 로깅
     */
    @Around("controllerMethods()")
    public Object logControllerMethodExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String category = "[" + className.replace("Controller", "") + "]";
        
        log.info("{} 요청 시작 - 메서드: {}", category, methodName);
        
        LocalDateTime startTime = LocalDateTime.now();
        Object result;
        
        try {
            // 메서드 실행
            result = joinPoint.proceed();
            
            long executionTime = ChronoUnit.MILLIS.between(startTime, LocalDateTime.now());
            log.info("{} 요청 완료 - 메서드: {}, 실행 시간: {}ms", 
                    category, methodName, executionTime);
            
            return result;
        } catch (Exception e) {
            log.error("{} 요청 오류 - 메서드: {}, 오류: {}", 
                    category, methodName, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * @LogExecutionTime 어노테이션이 붙은 메서드 실행 시간 로깅
     */
    @Around("logExecutionTimeAnnotation()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        LogExecutionTime annotation = method.getAnnotation(LogExecutionTime.class);
        
        String category = annotation.category();
        if (category.isEmpty()) {
            category = "[" + joinPoint.getTarget().getClass().getSimpleName() + "]";
        } else {
            category = "[" + category + "]";
        }
        
        String methodName = method.getName();
        log.info("{} 실행 시작 - 메서드: {}", category, methodName);
        
        LocalDateTime startTime = LocalDateTime.now();
        Object result;
        
        try {
            result = joinPoint.proceed();
            long executionTime = ChronoUnit.MILLIS.between(startTime, LocalDateTime.now());
            log.info("{} 실행 완료 - 메서드: {}, 실행 시간: {}ms", 
                    category, methodName, executionTime);
            
            return result;
        } catch (Exception e) {
            log.error("{} 실행 오류 - 메서드: {}, 오류: {}", 
                    category, methodName, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 결과의 크기를 반환
     */
    private int getResultSize(Object result) {
        if (result == null) {
            return 0;
        }
        
        if (result instanceof List<?>) {
            return ((List<?>) result).size();
        }
        
        if (result instanceof Map<?, ?>) {
            return ((Map<?, ?>) result).size();
        }
        
        return 1;
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
        
        return result.toString();
    }
} 