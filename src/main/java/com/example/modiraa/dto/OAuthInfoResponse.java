package com.example.modiraa.dto;

import com.example.modiraa.model.oauth.OAuthProvider;

public interface OAuthInfoResponse {
    Long getId();
    String getNickname();
    String getProfileImage();
    OAuthProvider getOAuthProvider();
}
