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
}
