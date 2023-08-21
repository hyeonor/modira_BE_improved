package com.example.modiraa.dto.response;

import com.example.modiraa.model.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageResponse {
    private ChatMessage.MessageType type;
    private String roomCode;
    private Long senderId;
    private String senderNickname;
    private String profileImage;
    private String message;
    private long currentParticipant;
}
