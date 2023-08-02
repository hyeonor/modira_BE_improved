package com.example.modiraa.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tb_like")
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "giver_user_id", nullable = false)
    private Member giver;

    @ManyToOne
    @JoinColumn(name = "receiver_user_id", nullable = false)
    private Member receiver;


    public Like(Member giver, Member receiver) {
        this.giver = giver;
        this.receiver = receiver;
    }
}