package com.example.modiraa.client;

import com.example.modiraa.dto.response.OAuthInfoResponse;
import com.example.modiraa.model.oauth.OAuthLoginParams;
import com.example.modiraa.model.oauth.OAuthProvider;

public interface OAuthApiClient {
    OAuthProvider oAuthProvider();
    String requestAccessToken(OAuthLoginParams params);
    OAuthInfoResponse requestOauthInfo(String accessToken);
}
