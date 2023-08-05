package com.example.modiraa.config.jwt;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class JwtProperties {
    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.prefix}")
    private String tokenPrefix;

    @Value("${jwt.subject}")
    private String subject;

    @Value("${jwt.claim.nickname}")
    private String nicknameClaim;

    @Value("${jwt.access.expiration}")
    private int accessExpiration;

    @Value("${jwt.access.header}")
    private String accessHeader;
}