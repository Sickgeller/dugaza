package kr.spring.api.client;

import com.fasterxml.jackson.databind.JsonNode;
import kr.spring.aop.LogExecutionTime;
import kr.spring.api.dto.ShoppingDetailApiDto;
import kr.spring.api.dto.TripCourseDetailApiDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.URI;

@RequiredArgsConstructor
@Component
@Slf4j
public class TripCourseApiClient {

    private final BaseApiClient baseApiClient;

    @LogExecutionTime(category = "TripCourseDetailData")
    public TripCourseDetailApiDto getTripCourseDetailData(Long contentId) {
        URI uri = baseApiClient.makeTourUri("/detailIntro2","contentId", String.valueOf(contentId), "contentTypeId", String.valueOf(25));
        return baseApiClient.callApi(uri, this::createTripCourseDetailApiDto).get(0);
    }

    private TripCourseDetailApiDto createTripCourseDetailApiDto(JsonNode item, String type) {
        return TripCourseDetailApiDto.builder()
                .contentId(item.path("contentid").asLong())  // 콘텐츠ID
                .contentTypeId(item.path("contenttypeid").asLong())  // 관광타입 ID (25 고정)
                .distance(item.path("distance").asText())  // 거리
                .infoCenterTourCourse(item.path("infocentertourcourse").asText())  // 문의 및 안내
                .schedule(item.path("schedule").asText())  // 여행 일정
                .takeTime(item.path("taketime").asText())  // 소요 시간
                .theme(item.path("theme").asText())  // 테마
                .build();
    }
}
