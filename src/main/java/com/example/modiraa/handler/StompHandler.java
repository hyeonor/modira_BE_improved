package com.example.modiraa.handler;

import com.example.modiraa.config.jwt.JwtAuthorizationFilter;
import com.example.modiraa.model.ChatMessage;
import com.example.modiraa.model.Member;
import com.example.modiraa.repository.UserRepository;
import com.example.modiraa.service.ChatRoomService;
import com.example.modiraa.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.Optional;


@Slf4j
@RequiredArgsConstructor
@Component
public class StompHandler implements ChannelInterceptor {

    private final JwtAuthorizationFilter jwtAuthorizationFilter;
    private final ChatRoomService chatRoomService;
    private final UserRepository userRepository;
    private final ChatService chatService;


    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        // websocket 연결시 헤더의 jwt token 검증
        if (StompCommand.CONNECT == accessor.getCommand()) {

            String jwtToken = accessor.getFirstNativeHeader("Authorization");
            log.info("CONNECT: {}", jwtToken);
            jwtAuthorizationFilter.validateToken(jwtToken);

        } else if (StompCommand.SUBSCRIBE == accessor.getCommand()) {

            // header정보에서 구독 destination정보를 얻고, roomId를 추출한다.
            String roomId = chatService.getRoomId(Optional.ofNullable((String) message.getHeaders().get("simpDestination")).orElse("InvalidRoomId"));
            String jwtToken = accessor.getFirstNativeHeader("Authorization");

            Member member;
            if (jwtToken != null) {
                //토큰으로 user 가져옴
                member = userRepository.findByNickname(jwtAuthorizationFilter.getUserNameFromJwt(jwtToken), Member.class)
                        .orElseThrow(()->new IllegalArgumentException("member 가 존재하지 않습니다."));
            }else {
                throw new IllegalArgumentException("유효하지 않은 token 입니다.");
            }

            Long memberId = member.getId();

            chatRoomService.setUserEnterInfo(memberId, roomId);

            // 채팅방의 인원수를 +1한다.
            chatRoomService.plusUserCount(roomId);

            // 클라이언트 입장 메시지를 채팅방에 발송한다.(redis publish)
            chatService.sendChatMessage(ChatMessage.builder()
                    .type(ChatMessage.MessageType.ENTER)
                    .roomId(roomId)
                    .sender(member.getNickname())
                    .build());

            log.info("SUBSCRIBED {}, {}", member.getNickname(), roomId);

        } else if (StompCommand.DISCONNECT == accessor.getCommand()) { // Websocket 연결 종료

            // 연결이 종료된 클라이언트 sesssionId로 채팅방 id를 얻는다.
            String sessionId = (String) message.getHeaders().get("simpSessionId");
            String roomId = chatRoomService.getUserEnterRoomId(sessionId);

            // 채팅방의 인원수를 -1한다.
            chatRoomService.minusUserCount(roomId);

            // 클라이언트 퇴장 메시지를 채팅방에 발송한다.(redis publish)
            String name = Optional.ofNullable((Principal) message.getHeaders().get("simpUser")).map(Principal::getName).orElse("UnknownUser");
            chatService.sendChatMessage(ChatMessage.builder().type(ChatMessage.MessageType.QUIT).roomId(roomId).sender(name).build());

            // 퇴장한 클라이언트의 roomId 맵핑 정보를 삭제한다.
            chatRoomService.removeUserEnterInfo(sessionId);

            log.info("DISCONNECTED {}, {}", sessionId, roomId);
        }
        return message;
    }
}
