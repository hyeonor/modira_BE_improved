package com.example.modiraa.repository;

import com.example.modiraa.model.Member;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.example.modiraa.model.QDislike.dislike;
import static com.example.modiraa.model.QLike.like;

@RequiredArgsConstructor
@Repository
public class LikeDislikeQueryRepository {
    private final JPAQueryFactory queryFactory;

    public Long calculateScore(Member receiver) {
        Long likeCount = queryFactory
                .select(like.receiver.count())
                .from(like)
                .where(like.receiver.eq(receiver))
                .fetchFirst();

        Long dislikeCount = queryFactory
                .select(dislike.receiver.count())
                .from(dislike)
                .where(dislike.receiver.eq(receiver))
                .fetchFirst();

        return likeCount - dislikeCount;
    }
}
