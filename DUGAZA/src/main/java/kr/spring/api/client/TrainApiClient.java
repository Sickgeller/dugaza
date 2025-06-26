package kr.spring.api.client;

import com.fasterxml.jackson.databind.JsonNode;
import kr.spring.api.dto.TrainCityApiDto;
import kr.spring.api.dto.TrainKindApiDto;
import kr.spring.api.dto.TrainRouteApiDto;
import kr.spring.api.dto.TrainStationApiDto;
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
public class TrainApiClient{

    private final BaseApiClient baseApiClient;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final DateTimeFormatter PARSING_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");


    /**
     * 기차 종류의 id와 이름 받아오는 메서드
     * @return TrainKindApiDto가 담긴 List
     */
    public List<TrainKindApiDto> getTrainKindData() {
        URI uri = baseApiClient.makeTrainUri("/getVhcleKndList");
        log.info("---------------> Train Kind Data Sync Client URI : {} ", uri);
        List<TrainKindApiDto> allResults = baseApiClient.callApi(uri, this::createTrainKindDto);
        log.info("---------------> Train Kind Data Sync Done Total : {} ", allResults.size());
        return allResults;
    }


    public List<TrainCityApiDto> getTrainAreaData() {
        URI uri = baseApiClient.makeTrainUri("/getCtyCodeList");
        log.info("---------------> Train Area Data Sync Client URI : {} ", uri);
        List<TrainCityApiDto> allResults = baseApiClient.callApi(uri, this::createTrainCityDto);
        log.info("---------------> Train Area Data Sync Done Total : {} ", allResults.size());
        return allResults;
    }

    public List<TrainStationApiDto> getTrainStationData(Long cityCode) {
        URI uri = baseApiClient.makeTrainUri("/getCtyAcctoTrainSttnList");
        log.info("---------------> Train Station Data Sync Client URI : {} ", uri);
        List<TrainStationApiDto> allResults = baseApiClient.callApi(uri, this::createTrainStationDto);
        log.info("---------------> Train Station Data Sync Done Total : {} ", allResults.size());
        return allResults;
    }


    public List<TrainRouteApiDto> getTrainRouteData(String depNodeId, String arrNodeId) {
        URI uri = baseApiClient.makeTrainUri("/getStrtpntAlocFndTrainInfo",
                "depPlaceId", depNodeId,
                "arrPlaceId", arrNodeId,
                "depPlandTime", PARSING_FORMATTER.format(LocalDateTime.now()));
        log.info("---------------> Train Route Data Sync Client URI : {} ", uri);
        List<TrainRouteApiDto> allResults = baseApiClient.callApiManyTimes(uri, this::createTrainRouteDto);
        log.info("---------------> Train Route Data Sync Done Total : {} ", allResults.size());
        return allResults;
    }


    private TrainRouteApiDto createTrainRouteDto(JsonNode item, String type) {
        return TrainRouteApiDto.builder()
                .adultCharge(item.path("adultcharge").asLong())
                .arrPlaceName(item.path("arrplacename").asText())
                .depPlaceName(item.path("depplacename").asText())
                .arrPlandTime(LocalDateTime.parse(item.path("arrplandtime").asText(), DATE_TIME_FORMATTER))
                .depPlandtime(LocalDateTime.parse(item.path("depplandtime").asText(), DATE_TIME_FORMATTER))
                .trainGradeName(item.path("traingradename").asText())
                .trainNo(item.path("trainno").asLong())
                .build();
    }


    private TrainStationApiDto createTrainStationDto(JsonNode item, String type) {
        return TrainStationApiDto.builder()
                .nodeId(item.path("nodeid").asText())
                .nodeName(item.path("nodename").asText())
                .build();
    }


    private TrainKindApiDto createTrainKindDto(JsonNode item, String type) {
        return TrainKindApiDto.builder()
                .vehicleKindId(item.path("vehiclekndid").asText())
                .vehicleKindNm(item.path("vehiclekndnm").asText())
                .isActive(1L)  // 기본값 설정
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
    private TrainCityApiDto createTrainCityDto(JsonNode item, String type) {
        return TrainCityApiDto.builder()
                .cityCode(item.path("citycode").asLong())
                .cityName(item.path("cityname").asText())
                .build();
    }


}
