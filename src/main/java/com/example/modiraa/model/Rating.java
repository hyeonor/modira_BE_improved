package com.example.modiraa.model;

import com.example.modiraa.enums.RatingType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rating_id", nullable = false)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "rating_type", nullable = false)
    private RatingType ratingType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "giver_member_id", nullable = false)
    private Member giver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_member_id", nullable = false)
    private Member receiver;


    public Rating(RatingType ratingType, Member giver, Member receiver) {
        this.ratingType = ratingType;
        this.giver = giver;
        this.receiver = receiver;
    }
}
