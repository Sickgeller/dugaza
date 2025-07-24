package kr.spring.api.client.base;

import java.net.URI;
import java.util.List;
import java.util.function.BiFunction;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * API 클라이언트 기본 인터페이스
 * RestClient와 WebClient 구현체가 공통으로 사용할 메서드들을 정의
 */
public interface BaseApiClient {

    /**
     * API 호출 메서드
     * @param uri API URI
     * @param dtoCreator DTO 생성 함수
     * @return API 응답 데이터 리스트
     */
    <T> List<T> callApi(URI uri, BiFunction<JsonNode, String, T> dtoCreator);

    /**
     * 여러 페이지의 API 호출 메서드
     * @param uri API URI
     * @param dtoCreator DTO 생성 함수
     * @return 모든 페이지의 API 응답 데이터 리스트
     */
    <T> List<T> callApiManyTimes(URI uri, BiFunction<JsonNode, String, T> dtoCreator);

    /**
     * 관광 API URI 생성
     * @param path API 경로
     * @param params 추가 파라미터들
     * @return 완성된 URI
     */
    URI makeTourUri(String path, String... params);

    /**
     * 기차 API URI 생성
     * @param path API 경로
     * @param params 추가 파라미터들
     * @return 완성된 URI
     */
    URI makeTrainUri(String path, String... params);

    /**
     * 고속버스 API URI 생성
     * @param path API 경로
     * @param params 추가 파라미터들
     * @return 완성된 URI
     */
    URI makeExpressBusUri(String path, String... params);
}
