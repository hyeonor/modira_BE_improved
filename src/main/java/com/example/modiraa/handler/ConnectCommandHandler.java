package com.example.modiraa.handler;

import com.example.modiraa.config.jwt.JwtAuthorizationFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;

@Slf4j
public class ConnectCommandHandler implements StompCommandHandler {
    private final JwtAuthorizationFilter jwtAuthorizationFilter;

    public ConnectCommandHandler(JwtAuthorizationFilter jwtAuthorizationFilter) {
        this.jwtAuthorizationFilter = jwtAuthorizationFilter;
    }

    @Override
    public void process(StompHeaderAccessor accessor) {
        String jwtToken = accessor.getFirstNativeHeader("Authorization");
        log.info("CONNECT: {}", jwtToken);
        jwtAuthorizationFilter.validateToken(jwtToken);
    }
}
