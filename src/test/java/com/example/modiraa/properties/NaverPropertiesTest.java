package com.example.modiraa.properties;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = NaverProperties.class)
class NaverPropertiesTest {
    @Autowired
    private NaverProperties naverProperties;

    @Test
    @DisplayName("네이버 프로퍼티 값 확인")
    void testProperties() {
        assertThat(naverProperties.getAuthUrl()).isEqualTo("https://nid.naver.com/oauth2.0/token");
        assertThat(naverProperties.getApiUrl()).isEqualTo("https://openapi.naver.com/v1/nid/me");
        assertThat(naverProperties.getGrantType()).isEqualTo("authorization_code");
        assertThat(naverProperties.getHeader()).isEqualTo("Authorization");
        assertThat(naverProperties.getPrefix()).isEqualTo("Bearer ");
    }
}