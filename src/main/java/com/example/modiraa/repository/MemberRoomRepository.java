package com.example.modiraa.repository;

import com.example.modiraa.dto.response.JoinedPostsResponse;
import com.example.modiraa.model.ChatRoom;
import com.example.modiraa.model.Member;
import com.example.modiraa.model.MemberRoom;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MemberRoomRepository extends JpaRepository<MemberRoom, Long> {
    // 내가 참석한 모임 조회
    @Query("SELECT NEW com.example.modiraa.dto.response.JoinedPostsResponse(p.id, p.title, PI.imageUrl, p.menu)" +
            "from MemberRoom M left outer join Post p on M.chatRoom = p.chatRoom left  outer join  PostImage  PI on PI.menu = p.menu " +
            "where M.member =:member and p.member <> :member " +
            " order by p.id desc")
    List<JoinedPostsResponse> MyJoinRead(@Param("member") Member member, Pageable pageable);

    Optional<MemberRoom> findByChatRoomAndMember(ChatRoom chatroom, Member member);
}
