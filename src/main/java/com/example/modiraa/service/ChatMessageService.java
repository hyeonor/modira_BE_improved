package com.example.modiraa.service;

import com.example.modiraa.dto.ChatMessageResponseDto;
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

    private final ChannelTopic channelTopic;
    private final RedisTemplate redisTemplate;
    private final ChatRoomService chatRoomService;
    private final ChatMessageQueryRepository chatMessageQueryRepository;

    // destination정보에서 roomId 추출
    public String getRoomId(String destination) {
        int lastIndex = destination.lastIndexOf('/');
        if (lastIndex != -1)
            return destination.substring(lastIndex + 1);
        else
            throw new IllegalArgumentException("lastIndex 오류입니다.");
    }

    // 채팅방에 메시지 발송
    public void sendChatMessage(ChatMessage chatMessage) {

        // 채팅방 인원수 세팅
        chatMessage.setUserCount(chatRoomService.getUserCount(chatMessage.getRoomId()));

        if (ChatMessage.MessageType.ENTER.equals(chatMessage.getType())) {
            chatMessage.setMessage(chatMessage.getSender().getNickname() + "님이 방에 입장했습니다.");
        } else if (ChatMessage.MessageType.QUIT.equals(chatMessage.getType())) {
            chatMessage.setMessage(chatMessage.getSender().getNickname() + "님이 방에서 나갔습니다.");
        }
        log.info("sender, sendMessage: {}, {}", chatMessage.getSender().getNickname(), chatMessage.getMessage());
        redisTemplate.convertAndSend(channelTopic.getTopic(), chatMessage);
    }

    // 채팅방의 마지막 150개 메세지를 페이징하여 리턴
    public Page<ChatMessageResponseDto> getChatMessageByRoomId(String roomId, Pageable pageable) {
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);
        Sort sort = Sort.by(Sort.Direction.DESC, "id" );
        pageable = PageRequest.of(page, 150, sort );
        Page<ChatMessage> chatMessages = chatMessageQueryRepository.findByRoomIdOrderByIdDesc(roomId, pageable);

        return chatResponseDto(chatMessages);
    }

    private Page<ChatMessageResponseDto> chatResponseDto(Page<ChatMessage> postSlice) {
        return postSlice.map(p ->
                ChatMessageResponseDto.builder()
                        .type(p.getType())
                        .roomId(p.getRoomId())
                        .senderId(p.getSender().getId())
                        .sender(p.getSender().getNickname())
                        .profileImage(p.getSender().getProfileImage())
                        .message(p.getMessage())
                        .userCount(p.getUserCount())
                        .build()
        );
    }

}
