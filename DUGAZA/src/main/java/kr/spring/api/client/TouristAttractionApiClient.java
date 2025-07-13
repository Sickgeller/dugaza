package kr.spring.api.client;


import com.fasterxml.jackson.databind.JsonNode;
import kr.spring.api.dto.TouristAttrationDetailApiDto;
import kr.spring.api.dto.TripCourseDetailApiDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.List;


@RequiredArgsConstructor
@Component
@Slf4j
public class TouristAttractionApiClient {

    private final BaseApiClient baseApiClient;
    
    public TouristAttrationDetailApiDto getTouristAttrationDetailData(Long contentId) {
        URI uri = baseApiClient.makeTourUri("/detailIntro2","contentId", String.valueOf(contentId), "contentTypeId", String.valueOf(12));
        List<TouristAttrationDetailApiDto> dtoList = baseApiClient.callApi(uri, this::createTouristAttrationDetailApiDto);
        if(!dtoList.isEmpty()){
            return dtoList.get(0);
        }
        return null;
    }

    private TouristAttrationDetailApiDto createTouristAttrationDetailApiDto(JsonNode item, String type) {
        String openDate = item.path("opendate").asText();
        return TouristAttrationDetailApiDto.builder()
                .contentId(item.path("contentid").asLong())  // 콘텐츠ID
                .contentTypeId(item.path("contenttypeid").asLong())  // 관광타입 ID (12 고정)
                .accomCount(item.path("accomcount").asLong())  // 수용인원
                .chkBabyCarriage(item.path("chkbabycarriage").asText())  // 유모차 대여 정보
                .chkCreditCard(item.path("chkcreditcard").asText())  // 신용카드 가능 정보
                .chkPet(item.path("chkpet").asText())  // 애완동물 동반 가능 정보
                .expAgeRange(item.path("expagerange").asText())  // 연령대
                .expGuide(item.path("expguide").asText())  // 안내 정보
                .heritage1(item.path("heritage1").asLong())  // 유산 정보 1
                .heritage2(item.path("heritage2").asLong())  // 유산 정보 2
                .heritage3(item.path("heritage3").asLong())  // 유산 정보 3
                .infoCenter(item.path("infocenter").asText())  // 문의 및 안내
                .openDate(openDate)  // 개장일
                .parking(item.path("parking").asText())  // 주차 시설
                .restDate(item.path("restdate").asText())  // 쉬는 날
                .useSeason(item.path("useseason").asText())  // 이용 시즌
                .useTime(item.path("usetime").asText())  // 이용 시간
                .build();
    }


}
