package kr.spring.api.client;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;

import kr.spring.aop.LogExecutionTime;
import kr.spring.api.dto.HouseApiDto;
import kr.spring.api.dto.HouseDetailApiDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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

    public HouseDetailApiDto getHouseDetailData(Long contentId) {
        URI uri = baseApiClient.makeTourUri("/detailIntro2","contentId", String.valueOf(contentId), "contentTypeId", String.valueOf(32));
        return baseApiClient.callApi(uri, this::createHouseDetailApiDto).get(0);
    }

    private HouseDetailApiDto createHouseDetailApiDto(JsonNode item, String type) {
        return HouseDetailApiDto.builder()
                .contentId(item.path("contentid").asLong())
                .contentTypeId(item.path("contenttypeid").asLong())
                .accomCountLodging(item.path("accomcountlodging").asLong())
                .checkInTime(item.path("checkintime").asText())
                .checkOutTime(item.path("checkouttime").asText())
                .chkCooking(item.path("chkcooking").asText())
                .foodPlace(item.path("foodplace").asText())
                .infoCenterLodging(item.path("infocenterlodging").asText())
                .parkingLodging(item.path("parkinglodging").asText())
                .pickup(item.path("pickup").asText())
                .roomCount(item.path("roomcount").asInt())
                .reservationLodging(item.path("reservationlodging").asText())
                .reservationUrl(item.path("reservationurl").asText())
                .roomType(item.path("roomtype").asText())
                .scaleLodging(item.path("scalelodging").asText())
                .subFacility(item.path("subfacility").asText())
                .barbecue(item.path("barbecue").asText())
                .beauty(item.path("beauty").asText())
                .beverage(item.path("beverage").asText())
                .bicycle(item.path("bicycle").asText())
                .campfire(item.path("campfire").asText())
                .fitness(item.path("fitness").asText())
                .karaoke(item.path("karaoke").asText())
                .publicBath(item.path("publicbath").asText())
                .publicPC(item.path("publicpc").asText())
                .sauna(item.path("sauna").asText())
                .seminar(item.path("seminar").asText())
                .sports(item.path("sports").asText())
                .refundRegulation(item.path("refundregulation").asText())
                .build();

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
