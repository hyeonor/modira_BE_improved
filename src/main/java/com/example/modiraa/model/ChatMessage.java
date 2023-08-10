package com.example.modiraa.model;

import com.example.modiraa.dto.request.ChatMessageRequest;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage extends Timestamped {

    // 메시지 타입 : 입장, 채팅, 퇴장
    public enum MessageType {
        ENTER, TALK, QUIT
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @Enumerated(value = EnumType.STRING)
    private MessageType type;

    // 채팅방 코드
    @Column
    private String roomCode;

    // 메시지
    @Column(length = 100000)
    private String message;

    // 채팅방 인원수
    @Column
    private long userCount;

    // 메시지 보낸사람
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member sender;

    @Builder
    public ChatMessage(MessageType type, String roomCode, Member sender, String message, long userCount) {
        this.type = type;
        this.roomCode = roomCode;
        this.sender = sender;
        this.message = message;
        this.userCount = userCount;
    }

    @Builder
    public ChatMessage(ChatMessageRequest chatMessageRequest) {
        this.type = chatMessageRequest.getType();
        this.roomCode = chatMessageRequest.getRoomCode();
        this.message = chatMessageRequest.getMessage();
        this.userCount = chatMessageRequest.getUserCount();
        this.sender = chatMessageRequest.getSender();
    }
}