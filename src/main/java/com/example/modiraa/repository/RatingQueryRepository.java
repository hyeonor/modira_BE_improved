package com.example.modiraa.repository;

import com.example.modiraa.enums.RatingType;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.example.modiraa.model.QRating.rating;

@Repository
@RequiredArgsConstructor
public class RatingQueryRepository {
    private final JPAQueryFactory queryFactory;

    public Long countLikesForReceiver(Long receiver) {
        return queryFactory
                .select(rating.receiver.count())
                .from(rating)
                .where(rating.receiver.id.eq(receiver)
                        .and(rating.ratingType.eq(RatingType.LIKE)))
                .fetchFirst();
    }

    public Long countDislikesForReceiver(Long receiver) {
        return queryFactory
                .select(rating.receiver.count())
                .from(rating)
                .where(rating.receiver.id.eq(receiver)
                        .and(rating.ratingType.eq(RatingType.DISLIKE)))
                .fetchFirst();
    }

    public Long calculateScore(Long receiver) {
        Long likes = countLikesForReceiver(receiver);
        Long dislikes = countDislikesForReceiver(receiver);

        return likes - dislikes;
    }
}
