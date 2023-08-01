package com.example.modiraa.controller;

import com.example.modiraa.dto.response.ChatMessageResponseDto;
import com.example.modiraa.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/chat")
public class ChatRoomController {

    private final ChatMessageService chatMessageService;

    // 해당 채팅방의 메세지 조회
    @GetMapping("/messages/{roomId}")
    public Page<ChatMessageResponseDto> getRoomMessage(@PathVariable String roomId, @PageableDefault Pageable pageable) {
        return chatMessageService.getChatMessageByRoomId(roomId, pageable);
    }

}