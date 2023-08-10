package com.example.modiraa.repository;

import com.example.modiraa.model.ChatMessage;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import static com.example.modiraa.model.QChatMessage.chatMessage;

@RequiredArgsConstructor
@Repository
public class ChatMessageQueryRepository {
    private final JPAQueryFactory queryFactory;

    public Page<ChatMessage> findByRoomCodeOrderByIdDesc(String roomCode, Pageable pageable) {
        QueryResults<ChatMessage> result = queryFactory.selectFrom(chatMessage)
                .where(chatMessage.roomCode.eq(roomCode))
                .orderBy(chatMessage.id.desc())
                .join(chatMessage.sender)
                .fetchJoin()
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();
        return new PageImpl<>(result.getResults(), pageable, result.getTotal());
    }
}
