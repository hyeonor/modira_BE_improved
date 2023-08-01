package com.example.modiraa.repository;

import com.example.modiraa.dto.response.EnterPostsResponseDto;
import com.example.modiraa.dto.response.JoinUserListResponseDto;
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
 @Query("SELECT NEW com.example.modiraa.dto.response.EnterPostsResponseDto(p.id, p.title, PI.imageurl, p.menu)" +
         "from MemberRoom M left outer join Post p on M.chatRoom = p.chatRoom left  outer join  PostImage  PI on PI.menu = p.menu " +
         "where M.member =:member and p.member <> :member "+
         " order by p.id desc")
 List<EnterPostsResponseDto> MyJoinRead(@Param("member")Member member, Pageable pageable);

    // 유저 리스트 정보 불러오기
    @Query("SELECT NEW com.example.modiraa.dto.response.JoinUserListResponseDto(m.id, m.nickname, m.profileImage)" +
    " from MemberRoom MR join MR.member m join MR.chatRoom c " +
    " where c = :chatRoom")
    List<JoinUserListResponseDto> RoomUserList(@Param("chatRoom")ChatRoom chatRoom);

    Optional<MemberRoom> findByChatRoomAndMember(Optional<ChatRoom> chatroom, Member member);

}
