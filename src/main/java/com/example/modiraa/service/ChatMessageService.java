package com.example.modiraa.service;

import com.example.modiraa.dto.request.ChatMessageRequest;
import com.example.modiraa.dto.response.ChatMessageResponse;
import com.example.modiraa.model.ChatMessage;
import com.example.modiraa.repository.ChatMessageQueryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatMessageService {
    private static final String ENTER_CHAT_ROOM = "님이 방에 입장했습니다.";
    private static final String QUIT_CHAT_ROOM = "님이 방에서 나갔습니다.";

    private final ChannelTopic channelTopic;
    private final RedisTemplate redisTemplate;
    private final ChatRoomService chatRoomService;
    private final ChatMessageQueryRepository chatMessageQueryRepository;

    // destination정보에서 roomCode 추출
    public String getRoomCode(String destination) {
        int lastIndex = destination.lastIndexOf('/');
        if (lastIndex != -1)
            return destination.substring(lastIndex + 1);
        else
            throw new IllegalArgumentException("lastIndex 오류입니다.");
    }

    // 채팅방에 메시지 발송
    public void sendChatMessage(ChatMessageRequest chatMessage) {
        // 채팅방 인원수 세팅
        long userCount = chatRoomService.getUserCount(chatMessage.getRoomCode());
        chatMessage.setUserCount(userCount);

        if (ChatMessage.MessageType.ENTER.equals(chatMessage.getType())) {
            chatMessage.setMessage(chatMessage.getSender().getNickname() + ENTER_CHAT_ROOM);
        } else if (ChatMessage.MessageType.QUIT.equals(chatMessage.getType())) {
            chatMessage.setMessage(chatMessage.getSender().getNickname() + QUIT_CHAT_ROOM);
        }

        log.info("sender, sendMessage: {}, {}", chatMessage.getSender().getNickname(), chatMessage.getMessage());
        redisTemplate.convertAndSend(channelTopic.getTopic(), chatMessage);
    }

    // 채팅방의 마지막 150개 메세지를 페이징하여 리턴
    public Page<ChatMessageResponse> getChatMessageByRoomCode(String roomCode, Pageable pageable) {
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        pageable = PageRequest.of(page, 150, sort);
        Page<ChatMessage> chatMessages = chatMessageQueryRepository.findByRoomCodeOrderByIdDesc(roomCode, pageable);

        return chatResponseDto(chatMessages);
    }

    private Page<ChatMessageResponse> chatResponseDto(Page<ChatMessage> postSlice) {
        return postSlice.map(p ->
                ChatMessageResponse.builder()
                        .type(p.getType())
                        .roomCode(p.getRoomCode())
                        .senderId(p.getSender().getId())
                        .sender(p.getSender().getNickname())
                        .profileImage(p.getSender().getProfileImage())
                        .message(p.getMessage())
                        .userCount(p.getUserCount())
                        .build()
        );
    }

}
