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

    @Column
    private String roomCode;

    @Column
    private long userCount;

    @Column(nullable = false)
    private int currentPeople;

    @Column(nullable = false)
    private int maxPeople;

    @OneToOne(mappedBy = "chatRoom")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_member_id")
    private Member ownerMember;

    public ChatRoom(Member ownerMember, int maxPeople) {
        this.roomCode = UUID.randomUUID().toString();
        this.currentPeople = 1;
        this.maxPeople = maxPeople;
        this.ownerMember = ownerMember;
    }

    public void updateCurrentPeople() {
        this.currentPeople = this.currentPeople + 1;
    }

    public void minusCurrentPeople() {
        this.currentPeople = this.currentPeople - 1;
    }
}