package com.example.modiraa.handler;

import com.example.modiraa.config.jwt.JwtAuthorizationFilter;
import com.example.modiraa.dto.request.ChatMessageRequest;
import com.example.modiraa.model.ChatMessage;
import com.example.modiraa.model.Member;
import com.example.modiraa.service.ChatMessageService;
import com.example.modiraa.service.ChatRoomService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;

import java.util.Optional;

@Slf4j
public class SubscribeCommandHandler implements StompCommandHandler {
    private final JwtAuthorizationFilter jwtAuthorizationFilter;
    private final ChatRoomService chatRoomService;
    private final ChatMessageService chatMessageService;

    public SubscribeCommandHandler(JwtAuthorizationFilter jwtAuthorizationFilter, ChatRoomService chatRoomService,
                                   ChatMessageService chatMessageService) {
        this.jwtAuthorizationFilter = jwtAuthorizationFilter;
        this.chatRoomService = chatRoomService;
        this.chatMessageService = chatMessageService;
    }

    @Override
    public void process(StompHeaderAccessor accessor) {
        String jwtToken = accessor.getFirstNativeHeader("Authorization");
        jwtAuthorizationFilter.validateToken(jwtToken);

        Member member = jwtAuthorizationFilter.getMemberFromJwt(jwtToken);

        // header 정보에서  sessionId를 얻는다.
        String sessionId = accessor.getSessionId();
        String roomCode = getRoomCode(accessor);

        // 채팅방에 들어온 클라이언트 sessionId, roomCode 맵핑 (특정 세션이 어떤 채팅방에 들어가 있는지 알기 위함)
        chatRoomService.setUserEnterInfo(sessionId, member.getId(), roomCode);
        chatRoomService.plusUserCount(roomCode); // 채팅방의 인원수를 +1
        sendEnterMessage(roomCode, member);

        log.info("SUBSCRIBED: sessionId={}, nickname={}, roomCode={}", sessionId, member.getNickname(), roomCode);
    }

    private String getRoomCode(StompHeaderAccessor accessor) {
        // header 정보에서 구독 destination 정보를 얻고, roomCode 추출
        String destination = accessor.getDestination();
        String defaultRoomCode = "InvalidRoomCode";
        return chatMessageService.getRoomCode(Optional.ofNullable(destination).orElse(defaultRoomCode));
    }

    /**
     * 클라이언트 입장 메시지를 채팅방에 발송한다. (redis publish)
     */
    private void sendEnterMessage(String roomCode, Member member) {
        chatMessageService.sendChatMessage(ChatMessageRequest.builder()
                .type(ChatMessage.MessageType.ENTER)
                .roomCode(roomCode)
                .sender(member)
                .build());
    }
}
