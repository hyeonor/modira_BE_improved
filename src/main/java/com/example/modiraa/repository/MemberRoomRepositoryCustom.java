package com.example.modiraa.repository;

import com.example.modiraa.dto.response.JoinedMembersResponse;
import com.example.modiraa.dto.response.JoinedPostsResponse;
import com.example.modiraa.model.Member;
import com.example.modiraa.model.MemberRoom;

import java.util.List;
import java.util.Optional;

public interface MemberRoomRepositoryCustom {
    List<MemberRoom> findByChatRoomId(Long chatroomId);

    Optional<MemberRoom> findTopByMemberOrderByIdDesc(Member member);

    Optional<MemberRoom> findByChatRoomIdAndMemberId(Long chatroomId, Long memberId);

    List<JoinedPostsResponse> findJoinedPostsByMember(Long memberId);

    List<JoinedMembersResponse> findJoinedMembersByMemberRoom(Long chatRoomId);
}
