package com.example.modiraa.repository;

import com.example.modiraa.model.ChatRoom;
import com.example.modiraa.model.RoomParticipant;
import com.example.modiraa.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRoomRepository extends JpaRepository<RoomParticipant, Long>, MemberRoomRepositoryCustom {
    Optional<RoomParticipant> findByChatRoomAndMember(ChatRoom chatroom, Member member);
}
