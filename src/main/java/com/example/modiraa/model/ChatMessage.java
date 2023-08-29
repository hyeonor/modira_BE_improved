package com.example.modiraa.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageType type;

    // 메시지
    @Column(length = 100000)
    private String message;

    // 채팅방 코드
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    // 메시지 보낸사람
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member sender;

    @Builder
    public ChatMessage(MessageType type, String message, ChatRoom chatRoom, Member sender) {
        this.type = type;
        this.message = message;
        this.chatRoom = chatRoom;
        this.sender = sender;
    }
}