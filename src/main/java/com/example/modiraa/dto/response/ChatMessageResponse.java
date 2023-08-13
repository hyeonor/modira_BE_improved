package com.example.modiraa.dto.response;

import com.example.modiraa.model.ChatMessage;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageResponse {
    private ChatMessage.MessageType type;
    private String roomCode;
    private Long senderId;
    private String sender;
    private String profileImage;
    private String message;
    private long userCount;
}
