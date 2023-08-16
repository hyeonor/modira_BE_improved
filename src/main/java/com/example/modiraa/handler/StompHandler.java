package com.example.modiraa.handler;

import com.example.modiraa.config.jwt.JwtAuthorizationFilter;
import com.example.modiraa.dto.request.ChatMessageRequest;
import com.example.modiraa.model.ChatMessage;
import com.example.modiraa.model.Member;
import com.example.modiraa.repository.MemberRepository;
import com.example.modiraa.service.ChatMessageService;
import com.example.modiraa.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;
import java.util.Optional;


@Slf4j
@RequiredArgsConstructor
@Component
public class StompHandler implements ChannelInterceptor {

    private final JwtAuthorizationFilter jwtAuthorizationFilter;
    private final ChatRoomService chatRoomService;
    private final MemberRepository memberRepository;
    private final ChatMessageService chatMessageService;


    @Override
    // 클라이언트가 메세지를 발송하면 최초에 해당 메세지를 인터셉트하여 처리함
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        StompCommand command = accessor.getCommand();

        // websocket 연결시 헤더의 jwt token 검증
        if (StompCommand.CONNECT == command) {
            handleConnect(accessor);
        } else if (StompCommand.SUBSCRIBE == command) {
            handleSubscribe(accessor, message);
        } else if (StompCommand.DISCONNECT == command) { // Websocket 연결 종료
            handleDisconnect(accessor);
        }
        return message;
    }

    private void handleConnect(StompHeaderAccessor accessor) {
        String jwtToken = accessor.getFirstNativeHeader("Authorization");
        log.info("CONNECT: {}", jwtToken);
        jwtAuthorizationFilter.validateToken(jwtToken);
    }

    private void handleSubscribe(StompHeaderAccessor accessor, Message<?> message) {
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

    private void handleDisconnect(StompHeaderAccessor accessor) {
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

    private void sendEnterMessage(String roomCode, Member member) {
        // 클라이언트 입장 메시지를 채팅방에 발송한다.(redis publish)
        chatMessageService.sendChatMessage(ChatMessageRequest.builder()
                .type(ChatMessage.MessageType.ENTER)
                .roomCode(roomCode)
                .sender(member)
                .build());
    }

    private void sendQuitMessage(String roomCode, Member member) {
        chatMessageService.sendChatMessage(ChatMessageRequest.builder()
                .type(ChatMessage.MessageType.QUIT)
                .roomCode(roomCode)
                .sender(member)
                .build());
    }
}
