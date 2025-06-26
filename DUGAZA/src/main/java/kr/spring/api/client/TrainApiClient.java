package kr.spring.api.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.project.dugaza.api.dto.TrainKindApiDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class TrainApiClient{

    private final BaseApiClient baseApiClient;

    /**
     * 기차 종류의 id와 이름 받아오는 메서드
     * @return TrainKindApiDto가 담긴 List
     */
    public List<TrainKindApiDto> getTrainKindData() {
        URI uri = baseApiClient.makeTrainUri("/getVhcleKndList");
        log.info("----------> Train Kind Data Sync Client URI : {} ", uri);
        List<TrainKindApiDto> allResults = baseApiClient.callApi(uri, this::createTrainKindDto);
        log.info("----------> Train Kind Data Sync Done Total : {} ", allResults.size());
        return baseApiClient.callApi(uri, this::createTrainKindDto);
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
}
