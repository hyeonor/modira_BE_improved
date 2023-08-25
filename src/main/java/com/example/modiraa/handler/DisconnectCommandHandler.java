package com.example.modiraa.handler;

import com.example.modiraa.dto.request.ChatMessageRequest;
import com.example.modiraa.model.ChatMessage;
import com.example.modiraa.model.Member;
import com.example.modiraa.service.ChatMessageService;
import com.example.modiraa.service.ChatRoomService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;


@Slf4j
public class DisconnectCommandHandler implements StompCommandHandler {
    private final ChatRoomService chatRoomService;
    private final ChatMessageService chatMessageService;

    public DisconnectCommandHandler(ChatRoomService chatRoomService, ChatMessageService chatMessageService) {
        this.chatRoomService = chatRoomService;
        this.chatMessageService = chatMessageService;
    }

    @Override
    public void process(StompHeaderAccessor accessor) {
        // 연결이 종료된 클라이언트 sesssionId로 채팅방 id를 얻는다.
        String sessionId = (String) accessor.getMessageHeaders().get("simpSessionId");
        String roomCode = chatRoomService.getUserEnterRoomCode(sessionId);

        // 저장했던 sessionId 로 유저 객체를 받아옴
        Member member = chatRoomService.checkSessionUser(sessionId);

        // 채팅방의 인원수를 -1
        chatRoomService.minusUserCount(roomCode);
        sendQuitMessage(roomCode, member);

        // 퇴장한 클라이언트의 roomCode 맵핑 정보를 삭제
        chatRoomService.removeUserEnterInfo(sessionId);

        log.info("DISCONNECTED: sessionId={}, nickname={}, roomCode={}", sessionId, member.getNickname(), roomCode);
    }

    private void sendQuitMessage(String roomCode, Member member) {
        chatMessageService.sendChatMessage(ChatMessageRequest.builder()
                .type(ChatMessage.MessageType.QUIT)
                .roomCode(roomCode)
                .sender(member)
                .build());
    }
}
