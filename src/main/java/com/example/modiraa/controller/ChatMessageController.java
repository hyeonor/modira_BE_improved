package com.example.modiraa.controller;

import com.example.modiraa.config.jwt.JwtAuthorizationFilter;
import com.example.modiraa.dto.request.ChatMessageRequest;
import com.example.modiraa.model.ChatMessage;
import com.example.modiraa.model.Member;
import com.example.modiraa.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

@RequiredArgsConstructor
@Controller
public class ChatMessageController {

    private final JwtAuthorizationFilter jwtAuthorizationFilter;
    private final ChatMessageService chatMessageService;

    // websocket "/pub/chat/message"로 들어오는 메시징을 처리
    @MessageMapping("/chat/message")
    public void message(@RequestBody ChatMessageRequest messageRequestDto, @Header("Authorization") String token) {
        Member member = jwtAuthorizationFilter.getMemberFromJwt(token);
        ChatMessage chatMessage = new ChatMessage(messageRequestDto);
        chatMessage.setSender(member);
        chatMessageService.sendChatMessage(chatMessage);
    }
}
