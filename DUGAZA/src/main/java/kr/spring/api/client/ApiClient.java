package kr.spring.api.client;

import com.fasterxml.jackson.databind.JsonNode;

import java.net.URI;
import java.util.List;
import java.util.function.BiFunction;


@Deprecated(since = "2025-06-26 over design")
public interface ApiClient {
    <T> List<T> callApi(URI uri, BiFunction<JsonNode, String, T> dtoCreator);
    <T> List<T> callApiManyTimes(URI uri, BiFunction<JsonNode, String, T> dtoCreator);
    URI makeUri(String baseUrl, String path, String... params);
}
