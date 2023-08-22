package com.example.modiraa.repository;

import com.example.modiraa.dto.response.JoinedMembersResponse;
import com.example.modiraa.dto.response.JoinedPostsResponse;
import com.example.modiraa.model.RoomParticipant;
import com.example.modiraa.model.Member;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.example.modiraa.model.QChatRoom.chatRoom;
import static com.example.modiraa.model.QMember.member;
import static com.example.modiraa.model.QChatRoomParticipant.chatRoomParticipant;
import static com.example.modiraa.model.QPost.post;
import static com.example.modiraa.model.QPostImage.postImage;

@RequiredArgsConstructor
@Repository
public class MemberRoomRepositoryImpl implements MemberRoomRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<RoomParticipant> findByChatRoomId(Long chatroomId) {
        return queryFactory.selectFrom(chatRoomParticipant)
                .where(chatRoomParticipant.chatRoom.id.eq(chatroomId))
                .join(chatRoomParticipant.member)
                .join(chatRoomParticipant.chatRoom)
                .fetchJoin()
                .fetch();
    }

    @Override
    public Optional<RoomParticipant> findTopByMemberOrderByIdDesc(Member member) {
        return Optional.ofNullable(queryFactory.selectFrom(chatRoomParticipant)
                .where(chatRoomParticipant.member.id.eq(member.getId()))
                .join(chatRoomParticipant.member)
                .join(chatRoomParticipant.chatRoom)
                .fetchJoin()
                .orderBy(chatRoomParticipant.id.desc())
                .fetchFirst());
    }

    @Override
    public Optional<RoomParticipant> findByChatRoomIdAndMemberId(Long chatroomId, Long memberId) {
        return Optional.ofNullable(queryFactory
                .selectFrom(chatRoomParticipant)
                .join(chatRoomParticipant.member, member)
                .join(chatRoomParticipant.chatRoom, chatRoom)
                .fetchJoin()
                .where(chatRoomParticipant.member.id.eq(memberId)
                        .and(chatRoomParticipant.chatRoom.id.eq(chatroomId))
                )
                .fetchOne());
    }

    @Override
    public List<JoinedPostsResponse> findJoinedPostsByMember(Long memberId) {
        return queryFactory
                .select(Projections.constructor(
                        JoinedPostsResponse.class,
                        post.id,
                        post.title,
                        postImage.imageUrl,
                        post.postImage.menu))
                .from(chatRoomParticipant)
                .leftJoin(post)
                .on(chatRoomParticipant.chatRoom.eq(post.chatRoom))
                .leftJoin(postImage)
                .on(postImage.menu.eq(post.postImage.menu))
                .where(chatRoomParticipant.member.id.eq(memberId)
                        .and(post.owner.id.ne(memberId))
                )
                .orderBy(post.id.desc())
                .fetch();
    }

    @Override
    public List<JoinedMembersResponse> findJoinedMembersByMemberRoom(Long chatRoomId) {
        return queryFactory
                .select(Projections.constructor(
                        JoinedMembersResponse.class,
                        member.id,
                        member.nickname,
                        member.profileImage))
                .from(chatRoomParticipant)
                .join(chatRoomParticipant.member, member)
                .join(chatRoomParticipant.chatRoom, chatRoom)
                .where(chatRoom.id.eq(chatRoomId))
                .fetch();
    }
}
