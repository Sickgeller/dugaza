package kr.spring.api.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
public class ContentDetailApiClient {

    private final BaseApiClient baseApiClient;

}
