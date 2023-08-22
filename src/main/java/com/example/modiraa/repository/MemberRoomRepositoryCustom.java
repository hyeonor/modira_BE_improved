package com.example.modiraa.repository;

import com.example.modiraa.dto.response.JoinedMembersResponse;
import com.example.modiraa.dto.response.JoinedPostsResponse;
import com.example.modiraa.model.RoomParticipant;
import com.example.modiraa.model.Member;

import java.util.List;
import java.util.Optional;

public interface MemberRoomRepositoryCustom {
    List<RoomParticipant> findByChatRoomId(Long chatroomId);

    Optional<RoomParticipant> findTopByMemberOrderByIdDesc(Member member);

    Optional<RoomParticipant> findByChatRoomIdAndMemberId(Long chatroomId, Long memberId);

    List<JoinedPostsResponse> findJoinedPostsByMember(Long memberId);

    List<JoinedMembersResponse> findJoinedMembersByMemberRoom(Long chatRoomId);
}
