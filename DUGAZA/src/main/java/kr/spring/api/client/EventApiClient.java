package kr.spring.api.client;

import com.fasterxml.jackson.databind.JsonNode;
import kr.spring.aop.LogExecutionTime;
import kr.spring.api.dto.EventContentApiDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventApiClient {

    private final BaseApiClient baseApiClient;
    private static final int DEFAULT_PAGE_SIZE = 100;
    private static final int MAX_TOTAL_ITEMS = 50000; // API 최대 제한 (필요에 따라 조정)

    /**
     * 특정 연도 이후의 모든 이벤트 정보를 조회합니다.
     * 페이징 처리를 통해 여러 번 API를 호출하여 모든 결과를 가져옵니다.
     *
     * @param startYear 조회 시작 연도
     * @return 이벤트 정보 목록
     */
    @LogExecutionTime(category = "EventData")
    public List<EventContentApiDto> getEventContent(Long startYear) {
        List<EventContentApiDto> allResults = new ArrayList<>();
        AtomicInteger totalCount = new AtomicInteger(0);

        // 첫 페이지 요청으로 전체 개수 파악
        int pageNo = 1;
        URI firstPageUri = baseApiClient.makeTourUri(
                "/searchFestival",
                "eventStartDate", String.valueOf(startYear),
                "pageNo", String.valueOf(pageNo),
                "numOfRows", String.valueOf(DEFAULT_PAGE_SIZE));
        
        return baseApiClient.callApiManyTimes(firstPageUri, this::createEventContentDto);
    }

    private EventContentApiDto createEventContentDto(JsonNode item, String type) {
        // tel 필드 값 처리
        String telValue = item.path("tel").asText();
        
        return EventContentApiDto.builder()
                .addr1(item.path("addr1").asText())
                .addr2(item.path("addr2").asText())
                .zipcode(item.path("zipcode").asText())
                .cat1(item.path("cat1").asText())
                .cat2(item.path("cat2").asText())
                .cat3(item.path("cat3").asText())
                .contentid(item.path("contentid").asText())
                .contenttypeid(item.path("contenttypeid").asText())
                .createdtime(item.path("createdtime").asText())
                .eventstartdate(item.path("eventstartdate").asText())
                .eventenddate(item.path("eventenddate").asText())
                .firstimage(item.path("firstimage").asText())
                .firstimage2(item.path("firstimage2").asText())
                .cpyrhtDivCd(item.path("cpyrhtDivCd").asText())
                .mapx(item.path("mapx").asText())
                .mapy(item.path("mapy").asText())
                .mlevel(item.path("mlevel").asText())
                .modifiedtime(item.path("modifiedtime").asText())
                .areacode(item.path("areacode").asText())
                .sigungucode(item.path("sigungucode").asText())
                .tel(telValue.length() > 50 ? telValue.substring(0, 50) : telValue) // tel 필드 값을 50자로 제한
                .title(item.path("title").asText())
                .build();
    }
}