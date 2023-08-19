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
    @Column(name = "chatroom_id")
    private Long id;

    @Column(nullable = false)
    private String roomCode;

    @Column(nullable = false)
    private int currentPeople;

    @Column(nullable = false)
    private int maxParticipant;

    @OneToOne(mappedBy = "chatRoom")
    private Post post;

    public ChatRoom(int maxParticipant) {
        this.roomCode = UUID.randomUUID().toString();
        this.currentPeople = 1;
        this.maxParticipant = maxParticipant;
    }

    public void updateCurrentPeople() {
        this.currentPeople = this.currentPeople + 1;
    }

    public void minusCurrentPeople() {
        this.currentPeople = this.currentPeople - 1;
    }
}