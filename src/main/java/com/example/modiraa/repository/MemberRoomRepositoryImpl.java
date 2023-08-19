package com.example.modiraa.repository;

import com.example.modiraa.dto.response.JoinedMembersResponse;
import com.example.modiraa.dto.response.JoinedPostsResponse;
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
import static com.example.modiraa.model.QPost.post;
import static com.example.modiraa.model.QPostImage.postImage;

@RequiredArgsConstructor
@Repository
public class MemberRoomRepositoryImpl implements MemberRoomRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<MemberRoom> findByChatRoomId(Long chatroomId) {
        return queryFactory.selectFrom(memberRoom)
                .where(memberRoom.chatRoom.id.eq(chatroomId))
                .join(memberRoom.member)
                .join(memberRoom.chatRoom)
                .fetchJoin()
                .fetch();
    }

    @Override
    public Optional<MemberRoom> findTopByMemberOrderByIdDesc(Member member) {
        return Optional.ofNullable(queryFactory.selectFrom(memberRoom)
                .where(memberRoom.member.id.eq(member.getId()))
                .join(memberRoom.member)
                .join(memberRoom.chatRoom)
                .fetchJoin()
                .orderBy(memberRoom.id.desc())
                .fetchFirst());
    }

    @Override
    public Optional<MemberRoom> findByChatRoomIdAndMemberId(Long chatroomId, Long memberId) {
        return Optional.ofNullable(queryFactory
                .selectFrom(memberRoom)
                .join(memberRoom.member, member)
                .join(memberRoom.chatRoom, chatRoom)
                .fetchJoin()
                .where(memberRoom.member.id.eq(memberId)
                        .and(memberRoom.chatRoom.id.eq(chatroomId))
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
                .from(memberRoom)
                .leftJoin(post)
                .on(memberRoom.chatRoom.eq(post.chatRoom))
                .leftJoin(postImage)
                .on(postImage.menu.eq(post.postImage.menu))
                .where(memberRoom.member.id.eq(memberId)
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
                .from(memberRoom)
                .join(memberRoom.member, member)
                .join(memberRoom.chatRoom, chatRoom)
                .where(chatRoom.id.eq(chatRoomId))
                .fetch();
    }
}
