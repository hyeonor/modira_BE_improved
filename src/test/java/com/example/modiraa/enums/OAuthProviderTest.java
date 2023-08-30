package com.example.modiraa.enums;

import com.example.modiraa.dto.request.oauth.OAuthProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OAuthProviderTest {

    @Test
    @DisplayName("OAuthProvider Enum fromValue() Test")
    void testFromValue() {
        System.out.println("OAuthProvider.KAKAO = " + OAuthProvider.KAKAO);
        System.out.println("OAuthProvider.NAVER = " + OAuthProvider.NAVER);

        assertThat(OAuthProvider.fromValue("kakao")).isEqualTo(OAuthProvider.KAKAO);
        assertThat(OAuthProvider.fromValue("naver")).isEqualTo(OAuthProvider.NAVER);
    }

    @Test
    @DisplayName("OAuthProvider Enum getValue() Test")
    void testGetValue() {
        assertThat(OAuthProvider.KAKAO.getValue()).isEqualTo("kakao");
        assertThat(OAuthProvider.NAVER.getValue()).isEqualTo("naver");
    }
}