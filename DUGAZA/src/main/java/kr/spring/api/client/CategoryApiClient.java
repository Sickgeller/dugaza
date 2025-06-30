package kr.spring.api.client;

import com.fasterxml.jackson.databind.JsonNode;
import kr.spring.aop.LogExecutionTime;
import kr.spring.api.dto.CategoryCodeApiDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CategoryApiClient {

    private final BaseApiClient baseApiClient;

    /**
     * 대분류 카테고리 코드 목록을 조회합니다.
     * @return 대분류 카테고리 코드 담긴 List
     */
    @LogExecutionTime(category = "CategoryData")
    public List<CategoryCodeApiDto> getCategoryCode1() {
        URI uri = baseApiClient.makeTourUri("/categoryCode2");
        return baseApiClient.callApi(uri, this::createCategoryCodeDto);
    }

    /**
     * 중분류 카테고리 코드 목록을 조회합니다.
     * @param cat1 대분류 코드
     * @return 중분류 카테고리 코드 목록 담긴 List
     */
    @LogExecutionTime(category = "CategoryData")
    public List<CategoryCodeApiDto> getCategoryCode2(String cat1) {
        URI uri = baseApiClient.makeTourUri("/categoryCode2","cat1", cat1);
        return baseApiClient.callApi(uri, this::createCategoryCodeDto);
    }

    /**
     * 소분류 카테고리 코드 목록을 조회합니다.
     * @param cat1 대분류 코드
     * @param cat2 중분류 코드
     * @return 소분류 카테고리 코드 목록 담긴 List
     */
    @LogExecutionTime(category = "CategoryData")
    public List<CategoryCodeApiDto> getCategoryCode3(String cat1, String cat2) {
        URI uri = baseApiClient.makeTourUri("/categoryCode2","cat1", cat1, "cat2", cat2);
        return baseApiClient.callApi(uri, this::createCategoryCodeDto);
    }

    /**
     * 투어 콘텐츠 id에 맞는 대분류 조회
     * @param tourContentId (요구되는 tourContentId)
     * @return 해당 콘텐츠 id에 맞는 대분류
     */
    @LogExecutionTime(category = "CategoryData")
    public List<CategoryCodeApiDto> getCategoryCodeWithTourContentId(Long tourContentId) {
        URI uri = baseApiClient.makeTourUri("/categoryCode2","contentTypeId", String.valueOf(tourContentId));
        return baseApiClient.callApi(uri, this::createCategoryCodeDto);
    }

    /**
     * @param tourContentId 요구되는 tourContentId,
     * @param cat1Dto 파싱된 cat1아이디
     * @return cat2 해당되는 덩어리
     */
    @LogExecutionTime(category = "CategoryData")
    public List<CategoryCodeApiDto> getCategoryCodeWithTourContentIdCat2(Long tourContentId, CategoryCodeApiDto cat1Dto) {
        URI uri = baseApiClient.makeTourUri("/categoryCode2", "contentTypeId", String.valueOf(tourContentId), "cat1", cat1Dto.getCategoryCode());
        return baseApiClient.callApi(uri, this::createCategoryCodeDto);

    }

    @LogExecutionTime(category = "CategoryData")
    public List<CategoryCodeApiDto> getCategoryCodeWithTourContentIdCat3(Long tourContentId, CategoryCodeApiDto cat1Dto, CategoryCodeApiDto cat2Dto) {
        URI uri = baseApiClient.makeTourUri("/categoryCode2", "contentTypeId", String.valueOf(tourContentId),"cat1", cat1Dto.getCategoryCode(),  "cat2", cat2Dto.getCategoryCode());
        return baseApiClient.callApi(uri, this::createCategoryCodeDto);
    }

    /**
     * JsonNode에서 CategoryCodeDto 객체를 생성
     * @param item JSON 항목
     * @param type 항목 타입 (array 또는 single)
     * @return 생성된 CategoryCodeDto 객체
     */
    private CategoryCodeApiDto createCategoryCodeDto(JsonNode item, String type) {
        try {
            // 필수 필드 검증
            if (!item.has("code") || item.path("code").isNull() || 
                !item.has("name") || item.path("name").isNull()) {
                return null;
            }
            
            String code = item.path("code").asText();
            String name = item.path("name").asText();
            
            CategoryCodeApiDto dto = new CategoryCodeApiDto();
            dto.setCategoryCode(code);
            dto.setCategoryName(name);
            dto.setIsActive(1L); // 기본값 설정
            
            int codeLength = code.length();
            int codeLevel;
            String parentCode = null;
            
            if (codeLength <= 3) {
                // 대분류 (예: A01)
                codeLevel = 1;
                parentCode = "P";
            } else if (codeLength <= 5) {
                // 중분류 (예: A0101)
                codeLevel = 2;
                parentCode = code.substring(0, 3); // A01
            } else {
                // 소분류 (예: A010101)
                codeLevel = 3;
                parentCode = code.substring(0, 5); // A0101
            }
            
            dto.setCodeLevel(codeLevel);
            dto.setParentCode(parentCode);
            
            return dto;
        } catch (Exception e) {
            return null;
        }
    }
}