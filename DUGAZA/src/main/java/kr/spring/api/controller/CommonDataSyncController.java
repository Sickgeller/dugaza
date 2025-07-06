package kr.spring.api.controller;

import kr.spring.api.dto.AreaCodeApiDto;
import kr.spring.api.service.AreaDataSyncService;
import kr.spring.api.service.CategoryDataSyncService;
import kr.spring.api.service.ContentTypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/admin/common")
@Slf4j
@RequiredArgsConstructor
public class CommonDataSyncController {

    private final AreaDataSyncService areaDataSyncService;
    private final CategoryDataSyncService categoryDataSyncService;
    private final ContentTypeService contentTypeService;


    @GetMapping("/sync/first-init")
    public ResponseEntity<Map<String, Object>> syncFirstInit() {
        syncAreaCodes();
        syncSigunguCodes();
        syncCategoryCodes();
        updateContentTypeForLevel3Categories();
        return ResponseEntity.ok(Map.of("success", "다했다"));
    }

    @GetMapping("/sync/area-codes")
    public ResponseEntity<Map<String, Object>> syncAreaCodes() {
        log.info("지역코드 동기화 요청 받음");
        Map<String, Object> result = areaDataSyncService.syncAreaCodes();
        return ResponseEntity.ok(result);
    }
    
//    @GetMapping("/sync/sido-codes")
//    @ResponseBody
//    public ResponseEntity<String> syncSidoCodes() {
//        log.info("시도 코드 동기화 요청 받음");
//        areaDataSyncService.syncSidoCodes();
//        return ResponseEntity.ok("시도 코드 동기화가 완료되었습니다.");
//    }
    
    @GetMapping("/sync/sigungu-codes")
    public ResponseEntity<Map<String, Object>> syncSigunguCodes() {
        log.info("시군구 코드 동기화 요청 받음");
        Map<String, Object> result = areaDataSyncService.syncSigunguCodes();
        return ResponseEntity.ok(result);
    }
    
    /**
     * 모든 카테고리 코드를 동기화합니다.
     * @return 동기화 결과 메시지
     */
    @GetMapping("/sync/category-codes")
    public ResponseEntity<Map<String, Object>> syncCategoryCodes() {
        log.info("카테고리 코드 동기화 요청 받음");
        int totalCount = categoryDataSyncService.syncAllCategoryCodes();
        return ResponseEntity.ok(Map.of(
                "message", "카테고리 코드 동기화가 완료되었습니다.",
                "totalCount", totalCount
        ));
    }
    
    /**
     * 대분류 카테고리 코드를 동기화합니다.
     * @return 동기화 결과 메시지
     */
    @GetMapping("/sync/category-codes/level1")
    public ResponseEntity<Map<String, Object>> syncCategoryCode1() {
        log.info("대분류 카테고리 코드 동기화 요청 받음");
        int count = categoryDataSyncService.syncCategoryCode1();
        return ResponseEntity.ok(Map.of(
                "message", "대분류 카테고리 코드 동기화가 완료되었습니다.",
                "count", count
        ));
    }
    
    /**
     * 중분류 카테고리 코드를 동기화합니다.
     * @return 동기화 결과 메시지
     */
    @GetMapping("/sync/category-codes/level2")
    public ResponseEntity<Map<String, Object>> syncCategoryCode2() {
        log.info("중분류 카테고리 코드 동기화 요청 받음");
        int count = categoryDataSyncService.syncCategoryCode2();
        return ResponseEntity.ok(Map.of(
                "message", "중분류 카테고리 코드 동기화가 완료되었습니다.",
                "count", count
        ));
    }
    
    /**
     * 소분류 카테고리 코드를 동기화합니다.
     * @return 동기화 결과 메시지
     */
    @GetMapping("/sync/category-codes/level3")
    public ResponseEntity<Map<String, Object>> syncCategoryCode3() {
        log.info("소분류 카테고리 코드 동기화 요청 받음");
        int count = categoryDataSyncService.syncCategoryCode3();
        return ResponseEntity.ok(Map.of(
                "message", "소분류 카테고리 코드 동기화가 완료되었습니다.",
                "count", count
        ));
    }

    /**
     * 소분류(code_level=3) 카테고리의 contentTypeId를 TOUR_CONTENT_CODE 테이블의 값으로 업데이트합니다.
     * @return 업데이트 결과
     */
    @GetMapping("/sync/category-codes/content-type")
    public ResponseEntity<Map<String, Object>> updateContentTypeForLevel3Categories() {
        log.info("소분류 카테고리의 콘텐츠 타입 ID 업데이트 요청 받음");
        Map<String, Integer> result = contentTypeService.updateContentTypeForLevel3Categories();
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "소분류 카테고리의 콘텐츠 타입 ID 업데이트가 완료되었습니다.");
        response.put("successCount", result.getOrDefault("successCount", 0));
        response.put("failCount", result.getOrDefault("failCount", 0));
        response.put("totalCount", result.getOrDefault("totalCount", 0));
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 특정 카테고리 코드의 contentTypeId를 업데이트합니다.
     * @param categoryCode 카테고리 코드
     * @param contentTypeId 콘텐츠 타입 ID
     * @return 업데이트 결과
     */
    @GetMapping("/category/{categoryCode}/content-type")
    public ResponseEntity<Map<String, Object>> updateContentTypeForCategory(
            @PathVariable String categoryCode,
            @RequestParam Long contentTypeId) {
        log.info("카테고리 코드 [{}]의 콘텐츠 타입 ID 업데이트 요청 받음: {}", categoryCode, contentTypeId);
        boolean success = contentTypeService.updateContentTypeForCategory(categoryCode, contentTypeId);
        
        if (success) {
            return ResponseEntity.ok(Map.of(
                    "message", "카테고리 코드 [" + categoryCode + "]의 콘텐츠 타입 ID가 업데이트되었습니다.",
                    "success", true
            ));
        } else {
            return ResponseEntity.ok(Map.of(
                    "message", "카테고리 코드 [" + categoryCode + "]의 콘텐츠 타입 ID 업데이트에 실패했습니다.",
                    "success", false
            ));
        }
    }
}
