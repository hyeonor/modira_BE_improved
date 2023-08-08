package com.example.modiraa.repository;

import com.example.modiraa.dto.response.JoinedMembersResponse;
import com.example.modiraa.model.Member;
import com.example.modiraa.model.MemberRoom;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.example.modiraa.model.QChatRoom.chatRoom;
import static com.example.modiraa.model.QMember.member;
import static com.example.modiraa.model.QMemberRoom.memberRoom;

@RequiredArgsConstructor
@Repository
public class MemberRoomQueryRepository {
    private final JPAQueryFactory queryFactory;

    public List<MemberRoom> findByChatRoomId(Long chatroomId) {
        return queryFactory.selectFrom(memberRoom)
                .where(memberRoom.chatRoom.id.eq(chatroomId))
                .join(memberRoom.member)
                .join(memberRoom.chatRoom)
                .fetchJoin()
                .fetch();
    }

    public Optional<MemberRoom> findTopByMemberOrderByIdDesc(Member member) {
        return Optional.ofNullable(queryFactory.selectFrom(memberRoom)
                .where(memberRoom.member.id.eq(member.getId()))
                .join(memberRoom.member)
                .join(memberRoom.chatRoom)
                .fetchJoin()
                .orderBy(memberRoom.id.desc())
                .fetchFirst());
    }

    public Optional<MemberRoom> findByChatRoomIdAndMemberId(Long chatroomID, Long memberID) {
        return Optional.ofNullable(queryFactory.selectFrom(memberRoom)
                .where(memberRoom.member.id.eq(memberID).and(memberRoom.chatRoom.id.eq(chatroomID)))
                .join(memberRoom.member)
                .join(memberRoom.chatRoom)
                .fetchJoin()
                .fetchOne());
    }

    public List<JoinedMembersResponse> findJoinedMembersByMemberRoom(Long chatRoomId) {
        return queryFactory
                .select(Projections.constructor(
                        JoinedMembersResponse.class,
                        member.id,
                        member.nickname,
                        member.profileImage))
                .from(memberRoom)
                .join(memberRoom.member, member)
                .join(memberRoom.chatRoom, chatRoom)
                .where(chatRoom.id.eq(chatRoomId))
                .fetch();
    }
}
