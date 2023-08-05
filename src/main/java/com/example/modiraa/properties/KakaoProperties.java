package com.example.modiraa.properties;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class KakaoProperties {
    @Value("${oauth.kakao.url.auth}")
    private String authUrl;

    @Value("${oauth.kakao.url.api}")
    private String apiUrl;

    @Value("${oauth.kakao.client-id}")
    private String clientId;

    @Value("${oauth.kakao.grant-type}")
    private String grantType;

    @Value("${oauth.kakao.header}")
    private String header;

    @Value("${oauth.kakao.prefix}")
    private String prefix;
}
