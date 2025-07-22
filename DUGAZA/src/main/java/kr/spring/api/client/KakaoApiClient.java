package kr.spring.api.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class KakaoApiClient {

    private final WebClient webClient;

    @Value("${api.kakao.rest-api-key}")
    private String restApiKey;

    @Value("${api.kakao.client-secret}")
    private String clientSecret;

    public KakaoApiClient() {
        this.webClient = WebClient.builder()
                .baseUrl("https://kauth.kakao.com")
                .build();
    }

    /**
     * 카카오 액세스 토큰 발급
     */
    public Mono<Map<String, Object>> getAccessToken(String code, String redirectUri) {
        return webClient.post()
                .uri("/oauth/token")
                .header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")
                .bodyValue(String.format(
                    "grant_type=authorization_code&client_id=%s&client_secret=%s&redirect_uri=%s&code=%s",
                    restApiKey, clientSecret, redirectUri, code
                ))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {});
    }

    /**
     * 카카오 사용자 정보 조회
     */
    public Mono<Map<String, Object>> getUserInfo(String accessToken) {
        return WebClient.builder()
                .baseUrl("https://kapi.kakao.com")
                .build()
                .get()
                .uri("/v2/user/me")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {});
    }
}