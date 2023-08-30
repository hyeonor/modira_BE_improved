package com.example.modiraa.properties;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = KakaoProperties.class)
class KakaoPropertiesTest {
    @Autowired
    private KakaoProperties kakaoProperties;

    @Test
    @DisplayName("카카오 프로퍼티 값 확인")
    void testProperties() {
        assertThat(kakaoProperties.getAuthUrl()).isEqualTo("https://kauth.kakao.com/oauth/token");
        assertThat(kakaoProperties.getApiUrl()).isEqualTo("https://kapi.kakao.com/v2/user/me");
        assertThat(kakaoProperties.getGrantType()).isEqualTo("authorization_code");
        assertThat(kakaoProperties.getHeader()).isEqualTo("Authorization");
        assertThat(kakaoProperties.getPrefix()).isEqualTo("Bearer ");
    }
}