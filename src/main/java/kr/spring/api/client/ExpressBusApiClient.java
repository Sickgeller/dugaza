package kr.spring.api.client;

import java.net.URI;
import java.util.List;

import kr.spring.api.client.base.BaseApiClient;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;

import kr.spring.aop.LogExecutionTime;
import kr.spring.api.dto.ExpressBusCityApiDto;
import kr.spring.api.dto.ExpressBusGradeApiDto;
import kr.spring.api.dto.ExpressBusTerminalApiDto;
import lombok.RequiredArgsConstructor;
import kr.spring.api.dto.ExpressBusRouteApiDto;

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

    @LogExecutionTime(category = "RouteData")
    public List<ExpressBusGradeApiDto> getRouteData() {
        URI uri = baseApiClient.makeExpressBusUri("/getStrtpntAlocFndExpbusInfo");
        return baseApiClient.callApiManyTimes(uri, this::createExpressBusGradeApiDto);
    }

    @LogExecutionTime(category = "RouteSearch")
    public List<ExpressBusRouteApiDto> searchRoutes(String depTerminalId, String arrTerminalId, String depPlandTime) {
        URI uri = baseApiClient.makeExpressBusUri("/getStrtpntAlocFndExpbusInfo", 
            "depTerminalId", depTerminalId,
            "arrTerminalId", arrTerminalId,
            "depPlandTime", depPlandTime);
        return baseApiClient.callApi(uri, this::createExpressBusRouteApiDto);
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
                .address(item.path("address").asText())
                .cityCode(item.path("cityCode").asLong())
                .build();
    }


    private ExpressBusCityApiDto createExpressBusCityApiDto(JsonNode item, String type) {
        return ExpressBusCityApiDto.builder()
                .cityCode(item.get("cityCode").asLong())
                .cityName(item.get("cityName").asText())
                .build();
    }

    private ExpressBusRouteApiDto createExpressBusRouteApiDto(JsonNode item, String type) {
        // 시간 형식 변환 (YYYYMMDDHHMM -> HH:mm)
        String depTime = formatTime(item.path("depPlandTime").asText());
        String arrTime = formatTime(item.path("arrPlandTime").asText());
        
        return ExpressBusRouteApiDto.builder()
                .depPlaceNm(item.path("depPlaceNm").asText())
                .arrPlaceNm(item.path("arrPlaceNm").asText())
                .depPlandTimeStr(depTime)
                .arrPlandTimeStr(arrTime)
                .charge(item.path("charge").asText())
                .gradeNm(item.path("gradeNm").asText())
                .routeId(item.path("routeId").asText())
                .build();
    }
    
    /**
     * 시간 형식을 HH:mm으로 변환
     * @param timeStr YYYYMMDDHHMM 형식의 시간 문자열
     * @return HH:mm 형식의 시간 문자열
     */
    private String formatTime(String timeStr) {
        if (timeStr == null || timeStr.length() < 12) {
            return "";
        }
        // YYYYMMDDHHMM -> HH:mm
        String hour = timeStr.substring(8, 10);
        String minute = timeStr.substring(10, 12);
        return hour + ":" + minute;
    }
}
