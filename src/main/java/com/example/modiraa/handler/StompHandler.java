package com.example.modiraa.handler;

import com.example.modiraa.config.jwt.JwtAuthorizationFilter;
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

        // websocket 연결시 헤더의 jwt token 검증
        if (StompCommand.CONNECT == accessor.getCommand()) {

            String jwtToken = accessor.getFirstNativeHeader("Authorization");
            log.info("CONNECT: {}", jwtToken);
            jwtAuthorizationFilter.validateToken(jwtToken);

        } else if (StompCommand.SUBSCRIBE == accessor.getCommand()) {

            // header정보에서 구독 destination정보를 얻고, roomId를 추출한다.
            String roomId = chatMessageService.getRoomId(Optional.ofNullable((String) message.getHeaders().get("simpDestination")).orElse("InvalidRoomId"));

            // 채팅방에 들어온 클라이언트 sessionId를 roomId와 맵핑해 놓는다.(나중에 특정 세션이 어떤 채팅방에 들어가 있는지 알기 위함)
            String sessionId = (String) message.getHeaders().get("simpSessionId");
            log.info("sessionId: {}", sessionId);

            String jwtToken = accessor.getFirstNativeHeader("Authorization");

            Member member;
            if (jwtToken != null) {
                member = memberRepository.findByNickname(jwtAuthorizationFilter.getNicknameFromJwt(jwtToken), Member.class)
                        .orElseThrow(() -> new IllegalArgumentException("member 가 존재하지 않습니다."));
            } else {
                throw new IllegalArgumentException("유효하지 않은 token 입니다.");
            }

            Long memberId = member.getId();


            chatRoomService.setUserEnterInfo(sessionId, memberId, roomId);

            // 채팅방의 인원수를 +1한다.
            chatRoomService.plusUserCount(roomId);

            // 클라이언트 입장 메시지를 채팅방에 발송한다.(redis publish)
            chatMessageService.sendChatMessage(ChatMessage.builder()
                    .type(ChatMessage.MessageType.ENTER)
                    .roomId(roomId)
                    .sender(member)
                    .build());


            log.info("SUBSCRIBED {}, {}, {}", sessionId, member.getNickname(), roomId);

        } else if (StompCommand.DISCONNECT == accessor.getCommand()) { // Websocket 연결 종료
            // 연결이 종료된 클라이언트 sesssionId로 채팅방 id를 얻는다.
            String sessionId = (String) message.getHeaders().get("simpSessionId");
            String roomId = chatRoomService.getUserEnterRoomId(sessionId);

            // 저장했던 sessionId 로 유저 객체를 받아옴
            Member member = chatRoomService.checkSessionUser(sessionId);

            // 채팅방의 인원수를 -1한다.
            chatRoomService.minusUserCount(roomId);

            chatMessageService.sendChatMessage(ChatMessage.builder()
                    .type(ChatMessage.MessageType.QUIT)
                    .roomId(roomId)
                    .sender(member)
                    .build());

            // 퇴장한 클라이언트의 roomId 맵핑 정보를 삭제한다.
            chatRoomService.removeUserEnterInfo(sessionId);

            log.info("DISCONNECTED {}, {}, {}", sessionId, member.getNickname(), roomId);
        }
        return message;
    }
}
