package kr.spring.api.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "api")
@Component
@Getter
@Setter
public class ApiConfig {
    private String serviceKey;
    private TourApi tour;
    private TrainApi train;
    private ExpressBusApi expressBus;
    private String kakaoBaseUrl;

    @Getter
    @Setter
    public static class TourApi{
        private String baseUrl;
    }

    @Getter
    @Setter
    public static class TrainApi{
        private String baseUrl;
    }

    @Getter
    @Setter
    public static class ExpressBusApi{
        private String baseUrl;
    }

    @Getter
    @Setter
    public static class KakaoApi{
        private String baseUrl;
        private String serviceKey;
    }
}
