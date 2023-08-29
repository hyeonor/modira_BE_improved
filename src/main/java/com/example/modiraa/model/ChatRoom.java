package com.example.modiraa.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoom implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 채팅방 코드
    @Column(nullable = false)
    private String roomCode;

    // 현재 인원 수
    @Column(nullable = false)
    private int currentParticipant;

    // 참여 가능한 최대 인원 수
    @Column(nullable = false)
    private int maxParticipant;

    @OneToOne(mappedBy = "chatRoom")
    private Post post;

    public ChatRoom(int maxParticipant) {
        this.roomCode = UUID.randomUUID().toString();
        this.currentParticipant = 1;
        this.maxParticipant = maxParticipant;
    }

    public void updateCurrentPeople() {
        this.currentParticipant = this.currentParticipant + 1;
    }

    public void minusCurrentPeople() {
        this.currentParticipant = this.currentParticipant - 1;
    }
}