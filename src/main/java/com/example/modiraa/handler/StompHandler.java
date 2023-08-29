package com.example.modiraa.handler;

import com.example.modiraa.config.jwt.JwtAuthorizationFilter;
import com.example.modiraa.service.ChatMessageService;
import com.example.modiraa.service.ChatRoomService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;


@Slf4j
@Component
public class StompHandler implements ChannelInterceptor {
    private final Map<StompCommand, StompCommandHandler> commandHandlers = new HashMap<>();

    public StompHandler(JwtAuthorizationFilter jwtAuthorizationFilter, ChatRoomService chatRoomService, ChatMessageService chatMessageService) {
        commandHandlers.put(StompCommand.CONNECT, new ConnectCommandHandler(jwtAuthorizationFilter));
        commandHandlers.put(StompCommand.SUBSCRIBE, new SubscribeCommandHandler(jwtAuthorizationFilter, chatRoomService, chatMessageService));
        commandHandlers.put(StompCommand.DISCONNECT, new DisconnectCommandHandler(chatRoomService, chatMessageService));
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        StompCommand command = accessor.getCommand();

        StompCommandHandler handler = commandHandlers.get(command);
        if (handler != null) {
            handler.process(accessor);
        }

        return message;
    }
}
