package kr.spring.api.client;

import com.fasterxml.jackson.databind.JsonNode;
import kr.spring.aop.LogExecutionTime;
import kr.spring.api.dto.ExpressBusCityApiDto;
import kr.spring.api.dto.ExpressBusGradeApiDto;
import kr.spring.api.dto.ExpressBusTerminalApiDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ExpressBusApiClient {
    private final BaseApiClient baseApiClient;

    @LogExecutionTime(category = "CityData")
    public List<ExpressBusCityApiDto> getCityData() {
        URI uri = baseApiClient.makeExpressBusUri("/getCtyCodeList");
        return baseApiClient.callApi(uri, this::createExpressBusCityApiDto);
    }

    @LogExecutionTime(category = "TerminalData")
    public List<ExpressBusTerminalApiDto> getTerminalData() {
        URI uri = baseApiClient.makeExpressBusUri("/getExpBusTrminlList");
        return baseApiClient.callApiManyTimes(uri, this::createExpressBusTerminalApiDto);
    }

    @LogExecutionTime(category = "GradeData")
    public List<ExpressBusGradeApiDto> getGradeData() {
        URI uri = baseApiClient.makeExpressBusUri("/getExpBusGradList");
        return baseApiClient.callApiManyTimes(uri, this::createExpressBusGradeApiDto);
    }

    private ExpressBusGradeApiDto createExpressBusGradeApiDto(JsonNode jsonNode, String s) {
        return ExpressBusGradeApiDto.builder()
                .gradeId(jsonNode.path("gradeId").asLong())
                .gradeNm(jsonNode.path("gradeNm").asText())
                .build();
    }

    private ExpressBusTerminalApiDto createExpressBusTerminalApiDto(JsonNode item, String type) {
        return ExpressBusTerminalApiDto
                .builder()
                .terminalId(item.path("terminalId").asText())
                .terminalName((item.path("terminalNm").asText()))
                .build();
    }


    private ExpressBusCityApiDto createExpressBusCityApiDto(JsonNode item, String type) {
        return ExpressBusCityApiDto.builder()
                .cityCode(item.get("cityCode").asLong())
                .cityName(item.get("cityName").asText())
                .build();
    }

}
