package kr.spring.api.client;

import com.fasterxml.jackson.databind.JsonNode;
import kr.spring.aop.LogExecutionTime;
import kr.spring.api.dto.TourApiDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TourApiClient {

    private final BaseApiClient baseApiClient;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    @LogExecutionTime(category = "TourData")
    public List<TourApiDto> getTouristData(int contentTypeId) {
        URI uri = baseApiClient.makeTourUri("/areaBasedList2","contentTypeId", String.valueOf(contentTypeId));
        return baseApiClient.callApiManyTimes(uri, this::createTourApiDto);
    }

    private TourApiDto createTourApiDto(JsonNode node, String item) {
        String createdTimeStr = node.path("createdtime").asText();
        String modifiedTimeStr = node.path("modifiedtime").asText();

        LocalDateTime createdTime = null;
        LocalDateTime modifiedTime = null;

        try {
            if (!createdTimeStr.isEmpty()) {
                createdTime = LocalDateTime.parse(createdTimeStr, DATE_TIME_FORMATTER);
            }
            if (!modifiedTimeStr.isEmpty()) {
                modifiedTime = LocalDateTime.parse(modifiedTimeStr, DATE_TIME_FORMATTER);
            }
        } catch (Exception e) {
            System.err.println("날짜 파싱 오류: created=" + createdTimeStr + ", modified=" + modifiedTimeStr);
        }

        return TourApiDto.builder()
                .addr1(node.path("addr1").asText())
                .addr2(node.path("addr2").asText())
                .areaCode(node.path("areacode").asLong())
                .cat1(node.path("cat1").asText())
                .cat2(node.path("cat2").asText())
                .cat3(node.path("cat3").asText())
                .contentId(node.path("contentid").asLong())
                .contentTypeId(node.path("contenttypeid").asLong())
                .createdTime(createdTime)
                .modifiedTime(modifiedTime)
                .firstImage(node.path("firstimage").asText())
                .firstImage2(node.path("firstimage2").asText())
                .cpyrhtDivCd(node.path("cpyrhtDivCd").asText())
                .mapX(node.path("mapx").asDouble())
                .mapY(node.path("mapy").asDouble())
                .mlevel(node.path("mlevel").asInt())
                .sigunguCode(node.path("sigungucode").asLong())
                .tel(node.path("tel").asText())
                .title(node.path("title").asText())
                .zipcode(node.path("zipcode").asText())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

}
