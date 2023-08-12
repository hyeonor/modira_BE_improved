package com.example.modiraa.dto.request.oauth;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

public enum OAuthProvider {
    KAKAO("kakao"),
    NAVER("naver");

    private final String value;
    private static final Map<String, OAuthProvider> map = new HashMap<>();

    OAuthProvider(String value) {
        this.value = value;
    }

    static {
        for (OAuthProvider oAuthProvider : values()) {
            map.put(oAuthProvider.value, oAuthProvider);
        }
    }

    @JsonCreator
    public static OAuthProvider fromValue(String value) {
        return map.get(value);
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
