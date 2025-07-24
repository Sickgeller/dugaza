package kr.spring.api.service.impl;

import kr.spring.api.client.CategoryApiClient;
import kr.spring.api.dto.CategoryCodeApiDto;
import kr.spring.api.mapper.ContentTypeMapper;
import kr.spring.api.service.ContentTypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContentTypeServiceImpl implements ContentTypeService {

    private final ContentTypeMapper contentTypeMapper;
    private final CategoryApiClient categoryApiClient;


    @Override
    @Transactional
    public Map<String, Integer> updateContentTypeForLevel3Categories() {
        log.info("소분류(code_level=3) 카테고리의 contentTypeId 업데이트 시작");
        
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);
        AtomicInteger totalCount = new AtomicInteger(0);

        try {
            List<Long> tourContentIdList = contentTypeMapper.findAllContentTypeCodes();
            
            log.info("TOUR_CATEGORY_CODE 테이블에서 조회된 콘텐츠 타입 코드 수: {}", 
                    tourContentIdList != null ? tourContentIdList.size() : 0);
            
            if (tourContentIdList == null || tourContentIdList.isEmpty()) {
                log.warn("콘텐츠 타입 코드가 없습니다. TOUR_CATEGORY_CODE 테이블에 데이터가 없거나 테이블이 존재하지 않을 수 있습니다.");
                Map<String, Integer> emptyResult = new HashMap<>();
                emptyResult.put("successCount", 0);
                emptyResult.put("failCount", 0);
                emptyResult.put("totalCount", 0);
                return emptyResult;
            }

            for (Long tourContentId : tourContentIdList) {
                log.debug("콘텐츠 타입 ID [{}] 처리 시작", tourContentId);
                List<CategoryCodeApiDto> categoryCodeWithTourContentIdCat1 = categoryApiClient.getCategoryCodeWithTourContentId(tourContentId);
                
                log.debug("콘텐츠 타입 ID [{}]에 대한 대분류 카테고리 수: {}", 
                        tourContentId, categoryCodeWithTourContentIdCat1 != null ? categoryCodeWithTourContentIdCat1.size() : 0);
                
                if (categoryCodeWithTourContentIdCat1 == null || categoryCodeWithTourContentIdCat1.isEmpty()) {
                    log.warn("콘텐츠 타입 ID [{}]에 대한 대분류 카테고리가 없습니다.", tourContentId);
                    continue;
                }
                
                for (CategoryCodeApiDto cat1Dto : categoryCodeWithTourContentIdCat1) {
                    List<CategoryCodeApiDto> categoryCodeWithTourContentIdCat2 = categoryApiClient.getCategoryCodeWithTourContentIdCat2(tourContentId, cat1Dto);
                    
                    log.debug("대분류 [{}]에 대한 중분류 카테고리 수: {}", 
                            cat1Dto.getCategoryCode(), categoryCodeWithTourContentIdCat2 != null ? categoryCodeWithTourContentIdCat2.size() : 0);
                    
                    if (categoryCodeWithTourContentIdCat2 == null || categoryCodeWithTourContentIdCat2.isEmpty()) {
                        log.warn("대분류 [{}]에 대한 중분류 카테고리가 없습니다.", cat1Dto.getCategoryCode());
                        continue;
                    }
                    
                    for (CategoryCodeApiDto cat2Dto : categoryCodeWithTourContentIdCat2) {
                        List<CategoryCodeApiDto> categoryCodesCat3 = categoryApiClient.getCategoryCodeWithTourContentIdCat3(tourContentId, cat1Dto, cat2Dto);
                        
                        log.debug("중분류 [{}]에 대한 소분류 카테고리 수: {}", 
                                cat2Dto.getCategoryCode(), categoryCodesCat3 != null ? categoryCodesCat3.size() : 0);
                        
                        if (categoryCodesCat3 == null || categoryCodesCat3.isEmpty()) {
                            log.warn("중분류 [{}]에 대한 소분류 카테고리가 없습니다.", cat2Dto.getCategoryCode());
                            continue;
                        }
                        
                        totalCount.addAndGet(categoryCodesCat3.size());
                        
                        for (CategoryCodeApiDto dto : categoryCodesCat3) {
                            try {
                                int updated = contentTypeMapper.updateCategoryContentTypeId(dto.getCategoryCode(), tourContentId);
                                if (updated > 0) {
                                    successCount.incrementAndGet();
                                    log.debug("카테고리 코드 [{}]의 contentTypeId를 [{}]로 업데이트 성공", dto.getCategoryCode(), tourContentId);
                                } else {
                                    failCount.incrementAndGet();
                                    log.warn("카테고리 코드 [{}]의 contentTypeId 업데이트 실패 (영향받은 행 없음)", dto.getCategoryCode());
                                }
                            } catch (Exception e) {
                                failCount.incrementAndGet();
                                log.error("카테고리 코드 [{}]의 contentTypeId 업데이트 중 오류 발생: {}", dto.getCategoryCode(), e.getMessage(), e);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("소분류 카테고리의 contentTypeId 업데이트 중 예상치 못한 오류 발생", e);
        }
        
        Map<String, Integer> result = new HashMap<>();
        result.put("successCount", successCount.get());
        result.put("failCount", failCount.get());
        result.put("totalCount", totalCount.get());
        
        log.info("소분류(code_level=3) 카테고리의 contentTypeId 업데이트 완료. 성공: {}, 실패: {}, 총 처리: {}", 
                successCount.get(), failCount.get(), totalCount.get());
        
        return result;
    }

    @Override
    @Transactional
    public boolean updateContentTypeForCategory(String categoryCode, Long contentTypeId) {
        log.info("카테고리 코드 [{}]의 contentTypeId를 [{}]로 업데이트 시작", categoryCode, contentTypeId);
        
        try {
            int result = contentTypeMapper.updateCategoryContentTypeId(categoryCode, contentTypeId);
            
            if (result > 0) {
                log.info("카테고리 코드 [{}]의 contentTypeId 업데이트 성공", categoryCode);
                return true;
            } else {
                log.warn("카테고리 코드 [{}]의 contentTypeId 업데이트 실패 (영향받은 행 없음)", categoryCode);
                return false;
            }
        } catch (Exception e) {
            log.error("카테고리 코드 [{}]의 contentTypeId 업데이트 중 오류 발생: {}", categoryCode, e.getMessage(), e);
            return false;
        }
    }
} 