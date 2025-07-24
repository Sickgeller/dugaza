package kr.spring.api.client;

import java.net.URI;

import kr.spring.api.client.base.BaseApiClient;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;

import kr.spring.api.dto.CulturalCenterDetailApiDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Component
@Slf4j
public class CulturalCenterApiClient {
    private final BaseApiClient baseApiClient;

    public CulturalCenterDetailApiDto getCulturalCenterDetailData(Long contentId) {
        URI uri = baseApiClient.makeTourUri("/detailIntro2","contentId", String.valueOf(contentId), "contentTypeId", String.valueOf(14));
        return baseApiClient.callApi(uri, this::createCulturalCenterDetailApiDto).get(0);
    }

    private CulturalCenterDetailApiDto createCulturalCenterDetailApiDto(JsonNode item, String type) {
        return CulturalCenterDetailApiDto.builder()
                .contentId(item.path("contentid").asLong())  // 콘텐츠ID
                .contentTypeId(item.path("contenttypeid").asLong())  // 관광타입 ID
                .accomCountCulture(item.path("accomcountculture").asLong())  // 수용인원
                .chkBabyCarriageCulture(item.path("chkbabycarriageculture").asText())  // 유모차대여정보
                .chkCreditCardCulture(item.path("chkcreditcardculture").asText())  // 신용카드가능정보
                .chkPetCulture(item.path("chkpetculture").asText())  // 애완동물동반가능정보
                .discountInfo(item.path("discountinfo").asText())  // 할인정보
                .infoCenterCulture(item.path("infocenterculture").asText())  // 문의및안내
                .parkingCulture(item.path("parkingculture").asText())  // 주차시설
                .parkingFee(item.path("parkingfee").asText())  // 주차요금
                .restDateCulture(item.path("restdateculture").asText())  // 쉬는날
                .useFee(item.path("usefee").asText())  // 이용요금
                .useTimeCulture(item.path("usetimeculture").asText())  // 이용시간
                .scale(item.path("scale").asText())  // 규모
                .spendTime(item.path("spendtime").asText())  // 관람소요시간
                .build();
    }


}
