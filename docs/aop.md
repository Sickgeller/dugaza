# 🔍 DUGAZA AOP 시스템

- **로깅 추상화 적용전**
```java
@Transactional
    @Override
    public List<AreaCodeApiDto> syncAreaCodes() {
        log.info("-----지역코드 동기화 시작 (시) -----");
        try {
            List<AreaCodeApiDto> siCodes = areaCodeApiClient.getAreaCode();

            // NULL 값이나 빈 값 필터링
            siCodes = siCodes.stream()
                    .filter(code -> code.getAreaCode() != null)
                    .filter(code -> code.getAreaName() != null && !code.getAreaName().isEmpty())
                    .collect(Collectors.toList());

            log.info("유효한 지역코드 개수: {}", siCodes.size());

            for (AreaCodeApiDto element : siCodes) {
                try {
                    Long areaCode = element.getAreaCode();
                    String areaName = element.getAreaName();

                    // NULL 체크 추가
                    if (areaCode == null) {
                        log.warn("지역코드가 null입니다. 건너뜁니다: {}", element);
                        continue;
                    }

                    AreaCodeApiDto existing = areaCodeMapper.findByAreaCode(areaCode.toString());

                    if (existing == null) {
                        try {
                            log.debug("삽입 시도 전 DTO 상태: {}", element);
                            areaCodeMapper.insert(element);
                            log.info("시 코드 추가 : {} - {}", areaCode, areaName);
                        } catch (Exception e) {
                            log.error("시 코드 추가 실패 : {} - {}", areaCode, areaName, e);
                        }
                    } else {
                        try {
                            existing.setAreaName(areaName);
                            areaCodeMapper.update(existing);
                            log.info("시도 코드 업데이트: {} - {}", areaCode, areaName);
                        } catch (Exception e) {
                            log.error("시도 코드 업데이트 실패: {} - {}", areaCode, areaName, e);
                        }
                    }
                } catch (Exception e) {
                    log.error("시 코드 처리 실패 : {}", element, e);
                }
            }
            log.info("시 코드 처리 완료 - {}개", siCodes.size());
            return siCodes;
        } catch (Exception e) {
            log.error("시 코드 처리 실패  ", e);
            return new ArrayList<>();
        }
    }
```

- 로깅 추상화 이후
```java
    @Transactional
    @Override
    @LogExecutionTime(category = "AreaSync")
    public Map<String, Object> syncAreaCodes() {
            List<AreaCodeApiDto> dtoList = areaCodeApiClient.getAreaCode();
            return common.processDataListToDB(areaCodeMapper, dtoList);
        }

```

## 🎯 프로젝트 개요

DUGAZA는 **Aspect-Oriented Programming (AOP)**를 활용하여 **로깅, 성능 모니터링, 전역 모델 관리**를 추상화했습니다. 이를 통해 비즈니스 로직과 횡단 관심사를 효과적으로 분리하여 유지보수성과 가독성을 크게 향상시켰습니다.

### 🌟 핵심 특징
- **다층 로깅 시스템**: Controller, Service, Mapper, API Client별 세분화된 로깅
- **성능 모니터링**: 실행 시간 측정 및 분석
- **전역 모델 관리**: 인증된 사용자 정보 자동 주입
- **커스텀 어노테이션**: `@LogExecutionTime`으로 선택적 성능 측정
- **체계적인 포인트컷**: 계층별 정확한 메서드 타겟팅

---

## 🏗️ AOP 아키텍처 구조

### 📊 전체 AOP 시스템 구조도

```
┌─────────────────────────────────────────────────────────────┐
│                    DUGAZA AOP System                        │
├─────────────────────────────────────────────────────────────┤
│  🎯 Controller Layer AOP                                   │
│  ├── ControllerLoggingAspect                              │
│  ├── GlobalModelAdvice                                    │
│  └── Request/Response 로깅                                │
├─────────────────────────────────────────────────────────────┤
│  🔧 Service Layer AOP                                     │
│  ├── ServiceLoggingAspect                                 │
│  ├── @LogExecutionTime                                    │
│  └── Business Logic 로깅                                  │
├─────────────────────────────────────────────────────────────┤
│  📊 Data Access Layer AOP                                 │
│  ├── MapperLoggingAspect                                  │
│  ├── SQL 실행 로깅                                        │
│  └── Performance 모니터링                                 │
├─────────────────────────────────────────────────────────────┤
│  🌐 API Client Layer AOP                                  │
│  ├── LoggingAspect                                        │
│  ├── External API 로깅                                    │
│  └── Response 분석                                        │
└─────────────────────────────────────────────────────────────┘
```

---

## 🔧 핵심 컴포넌트

### 1. 🎯 Controller Layer AOP

#### **ControllerLoggingAspect - 웹 요청 로깅**
```java
@Aspect
@Component
@Slf4j
public class ControllerLoggingAspect {

    /**
     * 모든 컨트롤러 메서드에 대한 포인트컷
     */
    @Pointcut("execution(* kr.spring..*.controller..*Controller.*(..))")
    private void allControllerMethods() {}
    
    @Pointcut("@within(org.springframework.web.bind.annotation.RestController)")
    private void allRestControllerMethods() {}
    
    @Pointcut("@within(org.springframework.stereotype.Controller)")
    private void allControllerAnnotatedMethods() {}
    
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
        
        // 요청 정보 가져오기
        HttpServletRequest request = null;
        String requestURI = "";
        String httpMethod = "";
        
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                request = attributes.getRequest();
                requestURI = request.getRequestURI();
                httpMethod = request.getMethod();
            }
        } catch (Exception e) {
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
```

#### **GlobalModelAdvice - 전역 모델 관리**
```java
@ControllerAdvice
public class GlobalModelAdvice {
    
    // 페이지마다 공통적으로 포함되어있는 헤더에 현재 인증/인가중인 사용자의 정보가 나와야해서 
    // 계속 주입시켜줌
    
    @ModelAttribute
    public void addModelMemberAndSeller(
            Model model,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        if(userDetails != null) {
            if(userDetails.getSeller() != null) {
                model.addAttribute("seller", userDetails.getSeller());
            }
            if(userDetails.getMember() != null) {
                model.addAttribute("member", userDetails.getMember());
            }
        }
    }
}
```

### 2. 🔧 Service Layer AOP

#### **ServiceLoggingAspect - 서비스 로직 로깅**
```java
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
        
        if (result instanceof List<?>) {
            List<?> list = (List<?>) result;
            return String.format("List[%d items]", list.size());
        }
        
        if (result instanceof Map<?, ?>) {
            Map<?, ?> map = (Map<?, ?>) result;
            return String.format("Map[%d entries]", map.size());
        }
        
        return result.toString();
    }
}
```

### 3. 📊 Data Access Layer AOP

#### **MapperLoggingAspect - SQL 실행 로깅**
```java
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
```

### 4. 🌐 API Client Layer AOP

#### **LoggingAspect - API 호출 로깅**
```java
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
}
```

### 5. 🏷️ 커스텀 어노테이션

#### **LogExecutionTime - 선택적 성능 측정**
```java
/**
 * 메서드 실행 시간을 로깅하기 위한 어노테이션
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogExecutionTime {
    /**
     * 로그에 표시할 카테고리
     */
    String category() default "";
}
```

---

## 🚀 사용 방법

### 1. **기본 로깅 (자동 적용)**
```java
@RestController
@RequestMapping("/api/tour")
public class TourController {

    private final TourApiClient tourApiClient;

    // 자동으로 ControllerLoggingAspect가 로깅 수행
    @GetMapping("/areas")
    public ResponseEntity<List<AreaCodeApiDto>> getAreas() {
        List<AreaCodeApiDto> areas = tourApiClient.getAreaCodeData();
        return ResponseEntity.ok(areas);
    }
}
```

### 2. **선택적 성능 측정**
```java
@Service
public class TourServiceImpl implements TourService {

    // @LogExecutionTime으로 선택적 성능 측정
    @LogExecutionTime(category = "TourData")
    public List<AreaCodeApiDto> getAreaCodeData() {
        URI uri = baseApiClient.makeTourUri("/areaCode2");
        return baseApiClient.callApi(uri, this::createAreaCodeDto);
    }

    // 일반 메서드는 ServiceLoggingAspect가 자동 로깅
    public void processTourData(List<AreaCodeApiDto> data) {
        // 비즈니스 로직
    }
}
```

### 3. **전역 모델 자동 주입**
```html
<!-- Thymeleaf 템플릿에서 자동으로 사용 가능 -->
<div th:if="${member != null}">
    <p>안녕하세요, <span th:text="${member.name}">사용자</span>님!</p>
</div>

<div th:if="${seller != null}">
    <p>판매자: <span th:text="${seller.businessName}">업체명</span></p>
</div>
```

---

## 📊 로깅 출력 예시

### 🔍 **Controller 로깅**
```
[Tour] [REST API] 요청 시작 - URI: /api/tour/areas [GET], 메서드: getAreas, 파라미터: []
[Tour] [REST API] 요청 완료 - URI: /api/tour/areas [GET], 메서드: getAreas, 실행 시간: 245ms
```

### 🔧 **Service 로깅**
```
[Tour] 서비스 시작 - 메서드: getAreaCodeData, 파라미터: []
[Tour] 서비스 완료 - 메서드: getAreaCodeData, 실행 시간: 180ms, 결과: List[17 items]
```

### 📊 **Mapper 로깅 (TRACE 레벨)**
```
[Tour] SQL 실행 시작 - 메서드: selectAreaCodes, 파라미터: []
[Tour] SQL 실행 완료 - 메서드: selectAreaCodes, 실행 시간: 15ms, 결과: List[17 items]
```

### 🌐 **API Client 로깅**
```
[Tour] API 호출 시작 - 메서드: getAreaCodeData, 파라미터: []
[Tour] API 호출 완료 - 메서드: getAreaCodeData, 실행 시간: 165ms, 결과 크기: 17
```

### ⏱️ **성능 측정 로깅**
```
[TourData] 실행 시작 - 메서드: getAreaCodeData
[TourData] 실행 완료 - 메서드: getAreaCodeData, 실행 시간: 180ms
```

---

## 🎯 AOP의 강력한 분리 기능

### 🔄 **관심사 분리 (Separation of Concerns)**
```java
// 비즈니스 로직 (핵심 관심사)
@Service
public class TourServiceImpl {
    public List<AreaCodeApiDto> getAreaCodeData() {
        // 순수한 비즈니스 로직만 작성
        URI uri = baseApiClient.makeTourUri("/areaCode2");
        return baseApiClient.callApi(uri, this::createAreaCodeDto);
    }
}

// 로깅 로직 (횡단 관심사) - AOP로 자동 처리
@Aspect
@Component
public class ServiceLoggingAspect {
    @Around("execution(* kr.spring..*.service..*Service*.*(..))")
    public Object logServiceMethodExecution(ProceedingJoinPoint joinPoint) {
        // 로깅 로직이 비즈니스 로직과 완전히 분리됨
    }
}
```

### 🎨 **코드 재사용성**
```java
// 하나의 어노테이션으로 모든 계층에서 성능 측정 가능
@LogExecutionTime(category = "TourData")
public List<AreaCodeApiDto> getAreaCodeData() { ... }

@LogExecutionTime(category = "UserData")
public UserVO getUserInfo(Long userId) { ... }

@LogExecutionTime(category = "PaymentData")
public PaymentResult processPayment(PaymentRequest request) { ... }
```

### 🛡️ **유지보수성 향상**
```java
// 로깅 정책 변경 시 AOP 클래스만 수정하면 전체 시스템에 적용
@Around("allControllerMethods()")
public Object logControllerMethodExecution(ProceedingJoinPoint joinPoint) {
    // 로깅 형식 변경, 추가 정보 수집 등
    // 비즈니스 로직은 전혀 건드리지 않음
}
```

---

## 📈 로깅의 중요성

### 🔍 **문제 진단 및 디버깅**

```
// 실행 흐름 추적
[Tour] [REST API] 요청 시작 - URI: /api/tour/areas [GET]
[Tour] 서비스 시작 - 메서드: getAreaCodeData
[Tour] API 호출 시작 - 메서드: getAreaCodeData
[Tour] API 호출 완료 - 메서드: getAreaCodeData, 실행 시간: 165ms
[Tour] 서비스 완료 - 메서드: getAreaCodeData, 실행 시간: 180ms
[Tour] [REST API] 요청 완료 - URI: /api/tour/areas [GET], 실행 시간: 245ms
```

### 📊 **성능 모니터링**

```
// 성능 병목 지점 식별
[TourData] 실행 완료 - 메서드: getAreaCodeData, 실행 시간: 180ms
[UserData] 실행 완료 - 메서드: getUserInfo, 실행 시간: 45ms
[PaymentData] 실행 완료 - 메서드: processPayment, 실행 시간: 1200ms  // ⚠️ 성능 이슈
```

### 🚨 **오류 추적**

```
// 오류 발생 지점 및 원인 파악
[Tour] API 호출 오류 - 메서드: getAreaCodeData, 오류: Connection timeout
[Tour] 서비스 오류 - 메서드: getAreaCodeData, 실행 시간: 5000ms, 오류: Connection timeout
[Tour] [REST API] 요청 오류 - URI: /api/tour/areas [GET], 실행 시간: 5020ms, 오류: Connection timeout
```

---

## 🎉 결론

DUGAZA의 AOP 시스템은 **관심사를 효과적으로 분리**하여 코드의 품질과 유지보수성을 크게 향상시켰습니다:

### ✅ **성과**
- **관심사 분리**: 비즈니스 로직과 횡단 관심사 완벽 분리
- **체계적 로깅**: 계층별 세분화된 로깅 시스템
- **성능 모니터링**: 실시간 실행 시간 측정 및 분석
- **코드 재사용성**: 커스텀 어노테이션으로 선택적 기능 적용
- **유지보수성**: 로깅 정책 변경 시 AOP만 수정하면 전체 적용





이 시스템을 통해 개발기간동안 개발자는 **비즈니스 로직 구현**에 집중하고,

운영중에도 **체계적인 모니터링**을 통해 안정적인 서비스를 제공할 수 있습니다.

---