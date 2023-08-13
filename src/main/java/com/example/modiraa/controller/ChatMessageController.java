package com.example.modiraa.controller;

import com.example.modiraa.config.jwt.JwtAuthorizationFilter;
import com.example.modiraa.dto.request.ChatMessageRequest;
import com.example.modiraa.dto.response.ChatMessageResponse;
import com.example.modiraa.model.ChatMessage;
import com.example.modiraa.model.Member;
import com.example.modiraa.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatMessageController {

    private final ChatMessageService chatMessageService;
    private final JwtAuthorizationFilter jwtAuthorizationFilter;

    // websocket "/pub/chat/message"로 들어오는 메시징을 처리
    @MessageMapping("/chat/message")
    public void message(@RequestBody ChatMessageRequest messageRequestDto, @Header("Authorization") String token) {
        Member member = jwtAuthorizationFilter.getMemberFromJwt(token);
        ChatMessage chatMessage = new ChatMessage(messageRequestDto);
        chatMessage.setSender(member);
        chatMessageService.sendChatMessage(chatMessage);
    }

    // 해당 채팅방의 메세지 조회
    @GetMapping("/chat/message/{roomCode}")
    public Page<ChatMessageResponse> getRoomMessage(@PathVariable String roomCode, @PageableDefault Pageable pageable) {
        return chatMessageService.getChatMessageByRoomCode(roomCode, pageable);
    }
}
