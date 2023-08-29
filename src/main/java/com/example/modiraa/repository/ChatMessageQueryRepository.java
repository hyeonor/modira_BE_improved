package com.example.modiraa.repository;

import com.example.modiraa.model.ChatMessage;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.modiraa.model.QChatMessage.chatMessage;
import static com.example.modiraa.model.QChatRoom.chatRoom;

@RequiredArgsConstructor
@Repository
public class ChatMessageQueryRepository {
    private final JPAQueryFactory queryFactory;

    public Page<ChatMessage> findByRoomCodeOrderByIdDesc(String roomCode, Pageable pageable) {
        List<ChatMessage> result = queryFactory.selectFrom(chatMessage)
                .join(chatMessage.chatRoom).fetchJoin()
                .join(chatMessage.sender).fetchJoin()
                .where(chatMessage.chatRoom.roomCode.eq(roomCode))
                .orderBy(chatMessage.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(chatMessage.count())
                .from(chatMessage)
                .join(chatMessage.chatRoom, chatRoom)
                .where(chatRoom.roomCode.eq(roomCode));

        return PageableExecutionUtils.getPage(result, pageable, countQuery::fetchOne);
    }
}
