package com.example.modiraa.config.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class AuthTokensGenerator {
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtProperties jwtProperties;

    public AuthTokens generate(String nickname) {
        long now = (new Date()).getTime();
        long AccessTokenExpireTime = jwtProperties.getAccessExpiration();
        Date accessTokenExpiredAt = new Date(now + AccessTokenExpireTime);

        String accessToken = jwtTokenProvider.generate(nickname, accessTokenExpiredAt);

        return AuthTokens.of(accessToken, jwtProperties.getTokenPrefix(), AccessTokenExpireTime / 1000L);
    }

    public Long extractMemberId(String accessToken) {
        return Long.valueOf(jwtTokenProvider.extractSubject(accessToken));
    }
}
