package kr.spring.api.client;

import com.fasterxml.jackson.databind.JsonNode;
import kr.spring.aop.LogExecutionTime;
import kr.spring.api.dto.ExpressBusCityApiDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ExpressBusApiClient {
    private final BaseApiClient baseApiClient;

    @LogExecutionTime(category = "express-bus city data")
    public List<ExpressBusCityApiDto> getCityData() {
        URI uri = baseApiClient.makeExpressBusUri("/getCtyCodeList");
        return baseApiClient.callApi(uri, this::createExpressBusCityApiDto);
    }

    private ExpressBusCityApiDto createExpressBusCityApiDto(JsonNode item, String type) {
        return ExpressBusCityApiDto.builder()
                .cityCode(item.get("cityCode").asLong())
                .cityName(item.get("cityName").asText())
                .build();
    }
}
