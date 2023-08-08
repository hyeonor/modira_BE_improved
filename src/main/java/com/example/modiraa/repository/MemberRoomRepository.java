package com.example.modiraa.repository;

import com.example.modiraa.model.ChatRoom;
import com.example.modiraa.model.Member;
import com.example.modiraa.model.MemberRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRoomRepository extends JpaRepository<MemberRoom, Long> {
    Optional<MemberRoom> findByChatRoomAndMember(ChatRoom chatroom, Member member);
}
