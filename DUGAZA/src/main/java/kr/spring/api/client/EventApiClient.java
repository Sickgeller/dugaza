package kr.spring.api.client;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;

import kr.spring.aop.LogExecutionTime;
import kr.spring.api.dto.EventContentApiDto;
import kr.spring.api.dto.EventDetailApiDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventApiClient {
    private final BaseApiClient baseApiClient;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");


    /**
     * 특정 연도 이후의 모든 이벤트 정보를 조회합니다.
     * 페이징 처리를 통해 여러 번 API를 호출하여 모든 결과를 가져옵니다.
     *
     * @param startYear 조회 시작 연도
     * @return 이벤트 정보 목록
     */
    @LogExecutionTime(category = "EventData")
    public List<EventContentApiDto> getEventContent(Long startYear) {
        URI firstPageUri = baseApiClient.makeTourUri(
                "/searchFestival2",
                "eventStartDate", String.valueOf(startYear));

        return baseApiClient.callApiManyTimes(firstPageUri, this::createEventContentDto);
    }

    public EventDetailApiDto getEventDetailData(Long contentId) {
        URI uri = baseApiClient.makeTourUri("/detailIntro2","contentId", String.valueOf(contentId), "contentTypeId", String.valueOf(15));
        return baseApiClient.callApi(uri, this::createEventDetailApiDto).get(0);
    }

    public List<EventDetailApiDto> getEventContent2(Long startYear) {
        URI firstPageUri = baseApiClient.makeTourUri(
                "/searchFestival2",
                "eventStartDate", String.valueOf(startYear));

        return baseApiClient.callApiManyTimes(firstPageUri, this::createEventDetailApiDto);
    }

    private EventDetailApiDto createEventDetailApiDto(JsonNode item, String typs) {

        String startDateS = item.path("eventstartdate").asText();
        String endDateS = item.path("eventenddate").asText();
        LocalDateTime startDate = null;
        LocalDateTime endDate = null;

        if (!startDateS.isEmpty()) {
            try {
                startDate = LocalDate.parse(startDateS, DATE_FORMATTER).atStartOfDay();
            } catch (Exception e) {
                // AOP에서 예외 처리
            }
        }

        if (!endDateS.isEmpty()) {
            try {
                endDate = LocalDate.parse(endDateS, DATE_FORMATTER).atStartOfDay();
            } catch (Exception e) {
                // AOP에서 예외 처리
            }
        }


            return EventDetailApiDto.builder()
                .contentId(item.path("contentid").asLong())
                .contentTypeId(item.path("contenttypeid").asLong())
                .ageLimit(item.path("agelimit").asText())
                .bookingPlace(item.path("bookingplace").asText())
                .discountInfoFestival(item.path("discountinfofestival").asText())
                .eventEndDate(endDate)
                .eventHomePage(item.path("eventhomepage").asText())
                .eventPlace(item.path("eventplace").asText())
                .eventStartDate(startDate)
                .festivalGrade(item.path("festivalgrade").asText())
                .placeInfo(item.path("placeinfo").asText())
                .playTime(item.path("playtime").asText())
                .program(item.path("program").asText())
                .spendTimeFestival(item.path("spendtimefestival").asText())
                .sponsor1(item.path("sponsor1").asText())
                .sponsor1Tel(item.path("sponsor1tel").asText())
                .sponsor2(item.path("sponsor2").asText())
                .sponsor2Tel(item.path("sponsor2tel").asText())
                .subEvent(item.path("subevent").asText())
                .useTimeFestival(item.path("usetimefestival").asText())
                .build();
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