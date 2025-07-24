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
import java.util.stream.Collectors;

/**
 * MyBatis Mapper 메서드에 대한 로깅을 담당하는 AOP 어드바이스
 */
@Aspect
@Component
@Slf4j
public class MapperLoggingAspect {

    /**
     * 모든 Mapper 메서드에 대한 포인트컷
     */
    @Pointcut("execution(* kr.spring..*.mapper.*Mapper.*(..))")
    private void allMapperMethods() {}

    /**
     * Mapper 메서드 실행 전후 로깅
     */
    @Around("allMapperMethods()")
    public Object logMapperMethodExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        // 로그 레벨이 TRACE인 경우에만 상세 로깅
        if (!log.isTraceEnabled()) {
            return joinPoint.proceed();
        }
        
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String category = "[" + className.replace("Mapper", "") + "]";
        
        // 메서드 파라미터 로깅
        Object[] args = joinPoint.getArgs();
        String params = Arrays.stream(args)
                .map(arg -> arg != null ? arg.toString() : "null")
                .collect(Collectors.joining(", "));
        
        log.trace("{} SQL 실행 시작 - 메서드: {}, 파라미터: [{}]", 
                category, methodName, params);
        
        LocalDateTime startTime = LocalDateTime.now();
        Object result;
        
        try {
            // 메서드 실행
            result = joinPoint.proceed();
            
            long executionTime = ChronoUnit.MILLIS.between(startTime, LocalDateTime.now());
            
            // 결과 요약
            String resultSummary = result != null ? result.toString() : "null";
            log.trace("{} SQL 실행 완료 - 메서드: {}, 실행 시간: {}ms, 결과: {}", 
                    category, methodName, executionTime, resultSummary);
            
            return result;
        } catch (Exception e) {
            long executionTime = ChronoUnit.MILLIS.between(startTime, LocalDateTime.now());
            log.error("{} SQL 실행 오류 - 메서드: {}, 실행 시간: {}ms, 오류: {}", 
                    category, methodName, executionTime, e.getMessage(), e);
            throw e;
        }
    }
} 