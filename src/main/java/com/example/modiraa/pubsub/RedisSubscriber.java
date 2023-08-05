package com.example.modiraa.pubsub;

import com.example.modiraa.dto.response.ChatMessageResponse;
import com.example.modiraa.model.ChatMessage;
import com.example.modiraa.repository.ChatMessageRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisSubscriber {

    private final ObjectMapper objectMapper;
    private final SimpMessageSendingOperations messagingTemplate;
    private final ChatMessageRepository chatMessageRepository;


    // 클라이언트에서 메세지가 도착하면 해당 메세지를 messagingTemplate 으로 컨버팅하고 다른 구독자들에게 전송한뒤 해당 메세지를 DB에 저장함
    public void sendMessage(String publishMessage) {
        try {
            ChatMessage chatMessage = objectMapper.readValue(publishMessage, ChatMessage.class);

            messagingTemplate.convertAndSend("/sub/chat/room/" + chatMessage.getRoomId(), getPayload(chatMessage));

            ChatMessage message = new ChatMessage();
            message.setType(chatMessage.getType());
            message.setRoomId(chatMessage.getRoomId());
            message.setSender(chatMessage.getSender());
            message.setMessage(chatMessage.getMessage());
            message.setUserCount(chatMessage.getUserCount());
            chatMessageRepository.save(message);

        } catch (Exception e) {
            log.error("Exception {}", e);
        }
    }

    private ChatMessageResponse getPayload(ChatMessage chatMessage) {
        return ChatMessageResponse.builder()
                .type(chatMessage.getType())
                .roomId(chatMessage.getRoomId())
                .senderId(chatMessage.getSender().getId())
                .sender(chatMessage.getSender().getNickname())
                .profileImage(chatMessage.getSender().getProfileImage())
                .message(chatMessage.getMessage())
                .userCount(chatMessage.getUserCount())
                .build();
    }
}