package kr.spring.api.client;

import lombok.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class KakaoPlaceApiClient {

    private final WebClient webClient = WebClient.builder()
            .baseUrl("${data-config.api.kakao.baseUrl}")
            .defaultHeader("Authorization", "KakaoAK ${data-config.api.serviceKey}")
            .build();

//    @Value("${data-config.api.kakao.baseUrl}")
//    private final String baseUrl;
//    public getSearchPlaceData(String keyword){
//
//    }

}
