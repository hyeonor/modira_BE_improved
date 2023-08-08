package com.example.modiraa.properties;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class NaverProperties {
    @Value("${oauth.naver.url.auth}")
    private String authUrl;

    @Value("${oauth.naver.url.api}")
    private String apiUrl;

    @Value("${oauth.naver.client-id}")
    private String clientId;

    @Value("${oauth.naver.secret}")
    private String clientSecret;

    @Value("${oauth.naver.grant-type}")
    private String grantType;

    @Value("${oauth.naver.header}")
    private String header;

    @Value("${oauth.naver.prefix}")
    private String prefix;
}
