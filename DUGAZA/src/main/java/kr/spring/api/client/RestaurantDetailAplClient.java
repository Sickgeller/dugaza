package kr.spring.api.client;

import com.fasterxml.jackson.databind.JsonNode;
import kr.spring.api.dto.RestaurantDetailApiDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.URI;

@RequiredArgsConstructor
@Component
@Slf4j
public class RestaurantDetailAplClient {

    private final BaseApiClient baseApiClient;

    public RestaurantDetailApiDto getRestaurantDetailData(Long contentId) {
        URI uri = baseApiClient.makeTourUri("/detailIntro2","contentId", String.valueOf(contentId), "contentTypeId", String.valueOf(39));
        return baseApiClient.callApi(uri, this::createRestaurantDetailApiDto).get(0);
    }
    private RestaurantDetailApiDto createRestaurantDetailApiDto(JsonNode item, String type) {
        return RestaurantDetailApiDto.builder()
                .contentId(item.path("contentid").asLong())
                .contentTypeId(item.path("contenttypeid").asLong())
                .chkCreditCardFood(item.path("chkcreditcardfood").asText())
                .discountInfoFood(item.path("discountinfofood").asText())
                .firstMenu(item.path("firstmenu").asText())
                .infoCenterFood(item.path("infocenterfood").asText())
                .kidsFacility(item.path("kidsfacility").asText())
                .openDateFood(item.path("opendatefood").asText())
                .openTimeFood(item.path("opentimefood").asText())
                .packing(item.path("packing").asText())
                .parkingFood(item.path("parkingfood").asText())
                .reservationFood(item.path("reservationfood").asText())
                .restDateFood(item.path("restdatefood").asText())
                .scaleFood(item.path("scalefood").asText())
                .seat(item.path("seat").asInt())
                .smoking(item.path("smoking").asText())
                .treatMenu(item.path("treatmenu").asText())
                .lcnsNo(item.path("lcnsno").asText())
                .build();
    }
}
