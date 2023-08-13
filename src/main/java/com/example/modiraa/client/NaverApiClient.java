package com.example.modiraa.client;

import com.example.modiraa.dto.request.oauth.OAuthLoginParams;
import com.example.modiraa.dto.request.oauth.OAuthProvider;
import com.example.modiraa.dto.response.NaverInfoResponse;
import com.example.modiraa.dto.response.NaverTokens;
import com.example.modiraa.dto.response.OAuthInfoResponse;
import com.example.modiraa.properties.NaverProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class NaverApiClient implements OAuthApiClient {

    private static final String GRANT_TYPE = "grant_type";
    private static final String CLIENT_ID = "client_id";
    private static final String CLIENT_SECRET = "client_secret";
    private final NaverProperties properties;
    private final RestTemplate restTemplate;

    @Override
    public OAuthProvider oAuthProvider() {
        return OAuthProvider.NAVER;
    }

    @Override
    public String requestAccessToken(OAuthLoginParams params) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = params.makeBody();
        body.add(GRANT_TYPE, properties.getGrantType());
        body.add(CLIENT_ID, properties.getClientId());
        body.add(CLIENT_SECRET, properties.getClientSecret());

        HttpEntity<?> request = new HttpEntity<>(body, httpHeaders);

        NaverTokens response = restTemplate.postForObject(properties.getAuthUrl(), request, NaverTokens.class);

        assert response != null;
        return response.getAccessToken();
    }

    @Override
    public OAuthInfoResponse requestOauthInfo(String accessToken) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        httpHeaders.set(properties.getHeader(), properties.getPrefix() + accessToken);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();

        HttpEntity<?> request = new HttpEntity<>(body, httpHeaders);

        return restTemplate.postForObject(properties.getApiUrl(), request, NaverInfoResponse.class);
    }
}
