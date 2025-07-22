package kr.spring.api.client;

import java.net.URI;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;

import kr.spring.api.dto.ShoppingDetailApiDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ShoppingApiClient {

    private final BaseApiClient baseApiClient;

    public ShoppingDetailApiDto getShoppingDetailData(Long contentId) {
        URI uri = baseApiClient.makeTourUri("/detailIntro2","contentId", String.valueOf(contentId), "contentTypeId", String.valueOf(38));
        return baseApiClient.callApi(uri, this::createShoppingDetailApiDto).get(0);
    }

    private ShoppingDetailApiDto createShoppingDetailApiDto(JsonNode item, String type) {
        return ShoppingDetailApiDto.builder()
                .contentId(item.path("contentid").asLong())  // 콘텐츠ID
                .contentTypeId(item.path("contenttypeid").asLong())  // 관광타입 ID (38 고정)
                .chkBabyCarriageShopping(item.path("chkbabycarriageshopping").asText())  // 유모차대여정보
                .chkCreditCardShopping(item.path("chkcreditcardshopping").asText())  // 신용카드가능정보
                .chkPetShopping(item.path("chkpetshopping").asText())  // 애완동물동반가능정보
                .cultureCenter(item.path("culturecenter").asText())  // 문화센터 정보
                .fairDay(item.path("fairday").asText())  // 행사일
                .infoCenterShopping(item.path("infocentershopping").asText())  // 문의및안내
                .openDateShopping(item.path("opendateshopping").asText())  // 개장일
                .openTime(item.path("opentime").asText())  // 개장시간
                .parkingShopping(item.path("parkingshopping").asText())  // 주차시설
                .restDateShopping(item.path("restdateshopping").asText())  // 쉬는날
                .restroom(item.path("restroom").asText())  // 화장실
                .saleItem(item.path("saleitem").asText())  // 판매품목
                .saleItemCost(item.path("saleitemcost").asText())  // 판매품목 가격
                .scaleShopping(item.path("scaleshopping").asText())  // 규모
                .shopGuide(item.path("shopguide").asText())  // 쇼핑가이드
                .build();
    }


}
