package com.example.modiraa.dto.request;

import com.example.modiraa.model.ChatMessage;
import com.example.modiraa.model.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatMessageRequest {
    private ChatMessage.MessageType type;
    private String roomCode;
    private Member sender;
    private String message;
    private long userCount;
}
