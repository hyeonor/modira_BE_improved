package com.example.modiraa.handler;

import com.example.modiraa.config.jwt.JwtAuthorizationFilter;
import com.example.modiraa.dto.request.ChatMessageRequest;
import com.example.modiraa.model.ChatMessage;
import com.example.modiraa.model.Member;
import com.example.modiraa.repository.MemberRepository;
import com.example.modiraa.service.ChatMessageService;
import com.example.modiraa.service.ChatRoomService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;

import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
public class SubscribeCommandHandler implements StompCommandHandler {
    private final JwtAuthorizationFilter jwtAuthorizationFilter;
    private final ChatRoomService chatRoomService;
    private final MemberRepository memberRepository;
    private final ChatMessageService chatMessageService;

    public SubscribeCommandHandler(JwtAuthorizationFilter jwtAuthorizationFilter, ChatRoomService chatRoomService,
                                   MemberRepository memberRepository, ChatMessageService chatMessageService) {
        this.jwtAuthorizationFilter = jwtAuthorizationFilter;
        this.chatRoomService = chatRoomService;
        this.memberRepository = memberRepository;
        this.chatMessageService = chatMessageService;
    }

    @Override
    public void process(StompHeaderAccessor accessor, Message<?> message) {
        // header 정보에서 구독 destination 정보를 얻고, roomCode를 추출한다.
        String roomCode = chatMessageService.getRoomCode(
                Optional.ofNullable((String) message.getHeaders().get("simpDestination")).orElse("InvalidRoomCode"));
        // 채팅방에 들어온 클라이언트 sessionId를 roomCode와 맵핑해 놓는다.(나중에 특정 세션이 어떤 채팅방에 들어가 있는지 알기 위함)
        String sessionId = (String) message.getHeaders().get("simpSessionId");

        String jwtToken = accessor.getFirstNativeHeader("Authorization");
        if (jwtToken == null) {
            throw new IllegalArgumentException("유효하지 않은 token 입니다.");
        }

        String nickname = jwtAuthorizationFilter.getNicknameFromJwt(jwtToken);
        Member member = memberRepository.findByNickname(nickname, Member.class)
                .orElseThrow(() -> new NoSuchElementException("nickname에 해당하는 member가 존재하지 않습니다: " + nickname));

        chatRoomService.setUserEnterInfo(sessionId, member.getId(), roomCode);
        chatRoomService.plusUserCount(roomCode); // 채팅방의 인원수를 +1

        sendEnterMessage(roomCode, member);
        log.info("SUBSCRIBED: sessionId={}, nickname={}, roomCode={}", sessionId, member.getNickname(), roomCode);
    }

    private void sendEnterMessage(String roomCode, Member member) {
        // 클라이언트 입장 메시지를 채팅방에 발송한다.(redis publish)
        chatMessageService.sendChatMessage(ChatMessageRequest.builder()
                .type(ChatMessage.MessageType.ENTER)
                .roomCode(roomCode)
                .sender(member)
                .build());
    }
}
