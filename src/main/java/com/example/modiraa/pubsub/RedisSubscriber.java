package com.example.modiraa.pubsub;

import com.example.modiraa.dto.request.ChatMessageRequest;
import com.example.modiraa.dto.response.ChatMessageResponse;
import com.example.modiraa.model.ChatMessage;
import com.example.modiraa.repository.ChatMessageRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisSubscriber {

    private final ObjectMapper objectMapper;
    private final SimpMessageSendingOperations messagingTemplate;
    private final ChatMessageRepository chatMessageRepository;


    // 클라이언트에서 메세지가 도착하면 해당 메세지를 messagingTemplate 으로 컨버팅하고 다른 구독자들에게 전송한뒤 해당 메세지를 DB에 저장함
    public void sendMessage(String publishMessage) {
        try {
            ChatMessageRequest chatMessage = objectMapper.readValue(publishMessage, ChatMessageRequest.class);

            messagingTemplate.convertAndSend("/sub/chat/room/" + chatMessage.getRoomCode(), getPayload(chatMessage));

            ChatMessage message = ChatMessage.builder()
                    .type(chatMessage.getType())
                    .roomCode(chatMessage.getRoomCode())
                    .sender(chatMessage.getSender())
                    .message(chatMessage.getMessage())
                    .build();

            chatMessageRepository.save(message);

        } catch (Exception e) {
            log.error("Exception occurred while sending message {}", e.getMessage());
        }
    }

    private ChatMessageResponse getPayload(ChatMessageRequest chatMessage) {
        return ChatMessageResponse.builder()
                .type(chatMessage.getType())
                .roomCode(chatMessage.getRoomCode())
                .senderId(chatMessage.getSender().getId())
                .senderNickname(chatMessage.getSender().getNickname())
                .profileImage(chatMessage.getSender().getProfileImage())
                .message(chatMessage.getMessage())
                .currentParticipant(chatMessage.getCurrentParticipant())
                .build();
    }
}