package com.example.modiraa.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Dislike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dislike_id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "giver_user_id", nullable = false)
    private Member giver;

    @ManyToOne
    @JoinColumn(name = "receiver_user_id", nullable = false)
    private Member receiver;

    public Dislike(Member giver, Member receiver) {
        this.giver = giver;
        this.receiver = receiver;
    }
}