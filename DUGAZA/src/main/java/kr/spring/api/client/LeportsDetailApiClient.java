package kr.spring.api.client;

import com.fasterxml.jackson.databind.JsonNode;
import kr.spring.api.dto.LeportsDetailApiDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.URI;

@RequiredArgsConstructor
@Component
@Slf4j
public class LeportsDetailApiClient {

    private final BaseApiClient baseApiClient;

    public LeportsDetailApiDto getLeportsDetailData(Long contentId) {
        URI uri = baseApiClient.makeTourUri("/detailIntro2","contentId", String.valueOf(contentId), "contentTypeId", String.valueOf(28));
        return baseApiClient.callApi(uri, this::createLeportsDetailApiDto).get(0);
    }

    private LeportsDetailApiDto createLeportsDetailApiDto(JsonNode item, String type) {
        return LeportsDetailApiDto.builder()
                .contentId(item.path("contentid").asLong())
                .contentTypeId(item.path("contenttypeid").asLong())
                .accomCountLeports(item.path("accomcountleports").asLong())
                .chkBabyCarriageLeports(item.path("chkbabycarriageleports").asText())
                .chkCreditCardLeports(item.path("chkcreditcardleports").asText())
                .chkPetLeports(item.path("chkpetleports").asText())
                .expAgeRangeLeports(item.path("expagerangeleports").asText())
                .infoCenterLeports(item.path("infocenterleports").asText())
                .openPeriod(item.path("openperiod").asText())
                .parkingFeeLeports(item.path("parkingfeeleports").asText())
                .parkingLeports(item.path("parkingleports").asText())
                .reservation(item.path("reservation").asText())
                .restDateLeports(item.path("restdateleports").asText())
                .scaleLeports(item.path("scaleleports").asText())
                .useFeeLeports(item.path("usefeeleports").asText())
                .useTimeLeports(item.path("usetimeleports").asText())
                .build();
    }



}
