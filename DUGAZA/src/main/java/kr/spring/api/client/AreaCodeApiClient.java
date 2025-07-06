package kr.spring.api.client;

import com.fasterxml.jackson.databind.JsonNode;
import kr.spring.aop.LogExecutionTime;
import kr.spring.api.dto.AreaCodeApiDto;
import kr.spring.api.dto.SigunguCodeApiDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AreaCodeApiClient{

    private final BaseApiClient baseApiClient;

    /**
     * 지역 코드 목록을 조회합니다.
     * @return 지역 코드 목록
     */
    @LogExecutionTime(category = "AreaData")
    public List<AreaCodeApiDto> getAreaCode() {
        URI uri = baseApiClient.makeTourUri("/areaCode2");
        return baseApiClient.callApi(uri, this::createAreaCodeDto);
    }

    /**
     * 특정 지역의 시군구 코드 목록을 조회합니다.
     * @param areaCode 지역 코드
     * @return 시군구 코드 목록
     */
    @LogExecutionTime(category = "AreaData")
    public List<SigunguCodeApiDto> getSigunguCode(Long areaCode) {
        URI uri = baseApiClient.makeTourUri("/areaCode2", "areaCode" , String.valueOf(areaCode));
        List<SigunguCodeApiDto> sigunguCodeApiDtos = baseApiClient.callApi(uri, this::createSigunguCodeDto);
        for( SigunguCodeApiDto sigunguCodeApiDto : sigunguCodeApiDtos ){
            sigunguCodeApiDto.setAreaCode(areaCode);
        }
        return sigunguCodeApiDtos;
    }

    /**
     * JsonNode에서 AreaCodeDto 객체를 생성
     * @param item JSON 항목
     * @param type 항목 타입 (array 또는 single)
     * @return 생성된 AreaCodeDto 객체
     */
    private AreaCodeApiDto createAreaCodeDto(JsonNode item, String type) {
        try {
            AreaCodeApiDto dto = new AreaCodeApiDto();
            dto.setAreaCode(Long.parseLong(item.path("code").asText()));
            dto.setAreaName(item.path("name").asText());
            return dto;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * JsonNode에서 SigunguCodeDto 객체를 생성
     * @param item JSON 항목
     * @param type 항목 타입 (array 또는 single)
     * @return 생성된 SigunguCodeDto 객체
     */
    private SigunguCodeApiDto createSigunguCodeDto(JsonNode item, String type) {
        return SigunguCodeApiDto.builder()
                .sigunguCode(item.path("code").asLong())
                .sigunguName(item.path("name").asText())
                .build();

    }
}
