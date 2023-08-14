package com.example.modiraa.repository;

import com.example.modiraa.model.Member;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.example.modiraa.model.QMember.member;


@RequiredArgsConstructor
@Repository
public class MemberRepositoryImpl implements MemberRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Member> findByOAuthId(String oAuthId) {
        return Optional.ofNullable(queryFactory
                .selectFrom(member)
                .where(member.oAuthId.eq(oAuthId))
                .fetchOne()
        );
    }
}
