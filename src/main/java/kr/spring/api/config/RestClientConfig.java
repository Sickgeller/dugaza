package kr.spring.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
public class RestClientConfig {
    
    @Bean
    public RestClient restClient() {
        // 간단한 HTTP 요청 팩토리 설정
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(30000); // 30초
        requestFactory.setReadTimeout(60000);    // 60초
        
        // RestClient 빌더
        return RestClient.builder()
                .requestFactory(requestFactory)
                .defaultHeader("User-Agent", "Dugaza-API-Client")
                .build();
    }
} 