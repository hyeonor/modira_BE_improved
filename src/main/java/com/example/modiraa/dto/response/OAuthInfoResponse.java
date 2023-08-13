package com.example.modiraa.dto.response;

import com.example.modiraa.dto.request.oauth.OAuthProvider;

public interface OAuthInfoResponse {
    String getId();
    String getNickname();
    String getProfileImage();
    OAuthProvider getOAuthProvider();
}
