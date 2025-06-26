package kr.spring.api.service.impl;

import com.project.dugaza.api.client.CategoryApiClient;
import com.project.dugaza.api.dto.CategoryCodeApiDto;
import com.project.dugaza.api.mapper.ContentTypeMapper;
import com.project.dugaza.api.service.ContentTypeService;
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
        Map<String, Integer> result = new HashMap<>();
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        List<Long> tourContentIdList = contentTypeMapper.findAllContentTypeCodes();

        for(Long tourContentId : tourContentIdList){
            List<CategoryCodeApiDto> categoryCodeWithTourContentIdCat1 = categoryApiClient.getCategoryCodeWithTourContentId(tourContentId);
            for(CategoryCodeApiDto cat1Dto : categoryCodeWithTourContentIdCat1){
                List<CategoryCodeApiDto> categoryCodeWithTourContentIdCat2 = categoryApiClient.getCategoryCodeWithTourContentIdCat2(tourContentId, cat1Dto);
                for(CategoryCodeApiDto cat2Dto: categoryCodeWithTourContentIdCat2){
                    List<CategoryCodeApiDto> categoryCodesCat3 = categoryApiClient.getCategoryCodeWithTourContentIdCat3(tourContentId,cat1Dto, cat2Dto);
                    result.put("totalCount", categoryCodesCat3.size());
                    try {
                        for (CategoryCodeApiDto dtos : categoryCodesCat3) {
                            contentTypeMapper.updateCategoryContentTypeId(dtos.getCategoryCode(), tourContentId);
                            result.put("successCount", successCount.incrementAndGet());
                        }
                    } catch (Exception e) {
                        failCount.incrementAndGet();
                        result.put("failCount", failCount.get());
                        log.error("소분류 여행타입 아이디 업데이트 도중 에러 발생 {}", failCount.get(), e);
                    }
                }
            }
        }
        log.info("소분류(code_level=3) 카테고리의 contentTypeId 업데이트 완료");
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