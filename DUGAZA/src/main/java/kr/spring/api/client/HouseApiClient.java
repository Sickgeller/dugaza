package kr.spring.api.client;

import com.fasterxml.jackson.databind.JsonNode;
import kr.spring.aop.LogExecutionTime;
import kr.spring.api.dto.HouseApiDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class HouseApiClient{

    private final BaseApiClient baseApiClient;
    private static final DateTimeFormatter CREATED_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    @LogExecutionTime(category = "HouseData")
    public List<HouseApiDto> getHouseDataList() {
        URI uri = baseApiClient.makeTourUri("/searchStay2");
        return baseApiClient.callApiManyTimes(uri, this::createHouseDto);
    }

    private HouseApiDto createHouseDto(JsonNode item, String type) {
        String createdTimeStr = item.path("createdtime").asText();
        String updatedTimeStr = item.path("modifiedtime").asText();
        LocalDateTime createdTime = null;
        LocalDateTime updatedTime = null;
        if (!createdTimeStr.isEmpty()) {
            try {
                createdTime = LocalDateTime.parse(createdTimeStr, CREATED_TIME_FORMATTER);
            } catch (Exception e) {
                // AOP에서 예외 처리
            }
        }
        if (!updatedTimeStr.isEmpty()) {
            try {
                updatedTime = LocalDateTime.parse(updatedTimeStr, CREATED_TIME_FORMATTER);
            } catch (Exception e) {
                // AOP에서 예외 처리
            }
        }

        return HouseApiDto.builder()
                .addr1(item.path("addr1").asText())
                .addr2(item.path("addr2").asText())
                .areaCode(item.path("areacode").asLong())
                .cat1(item.path("cat1").asText())
                .cat2(item.path("cat2").asText())
                .cat3(item.path("cat3").asText())
                .contentId(item.path("contentid").asLong())
                .contentTypeId(item.path("contenttypeid").asLong())
                .createdAt(createdTime)
                .updatedAt(updatedTime)
                .firstImage(item.path("firstimage").asText())
                .firstImage2(item.path("firstimage2").asText())
                .cpyrhtDivCd(item.path("cpyrhtDivCd").asText())
                .mapX(item.path("mapx").asDouble())
                .mapY(item.path("mapy").asDouble())
                .mLevel(item.path("mlevel").asLong())
                .sigunguCode(item.path("sigungucode").asLong())
                .tel(item.path("tel").asText())
                .title(item.path("title").asText())
                .zipcode(item.path("zipcode").asLong())
                .build();
    }

}
