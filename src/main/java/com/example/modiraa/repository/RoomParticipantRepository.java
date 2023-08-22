package com.example.modiraa.repository;

import com.example.modiraa.model.ChatRoom;
import com.example.modiraa.model.Member;
import com.example.modiraa.model.RoomParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoomParticipantRepository extends JpaRepository<RoomParticipant, Long>, RoomParticipantRepositoryCustom {
    Optional<RoomParticipant> findByChatRoomAndMember(ChatRoom chatroom, Member member);
}
