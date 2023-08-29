package com.example.modiraa.handler;

import org.springframework.messaging.simp.stomp.StompHeaderAccessor;

public interface StompCommandHandler {
    void process(StompHeaderAccessor accessor);
}
