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
}