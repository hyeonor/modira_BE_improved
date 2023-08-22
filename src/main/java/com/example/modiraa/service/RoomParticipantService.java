package com.example.modiraa.service;

import com.example.modiraa.auth.UserDetailsImpl;
import com.example.modiraa.dto.response.JoinedMembersResponse;
import com.example.modiraa.enums.GenderType;
import com.example.modiraa.exception.CustomException;
import com.example.modiraa.exception.ErrorCode;
import com.example.modiraa.model.ChatRoom;
import com.example.modiraa.model.Member;
import com.example.modiraa.model.Post;
import com.example.modiraa.model.RoomParticipant;
import com.example.modiraa.repository.ChatRoomRepository;
import com.example.modiraa.repository.MemberRepository;
import com.example.modiraa.repository.PostRepository;
import com.example.modiraa.repository.RoomParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class RoomParticipantService {
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final RoomParticipantRepository roomParticipantRepository;

    //채팅 참여하기
    public ResponseEntity<?> enterRoom(UserDetailsImpl userDetails, String roomCode) {
        Member member = userDetails.getMember();
        ChatRoom chatroom = chatRoomRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_CODE_NOT_FOUND));
        Post post = postRepository.findByChatRoomId(chatroom.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        checkIfAlreadyJoined(member);
        checkForDuplicateJoin(chatroom, member);
        checkFullNumOfPeople(chatroom);

        int memberAge = member.getAge();
        GenderType memberGender = member.getGender();

        ageAndGenderConditionCheck(post, memberAge, memberGender);

        updatePostStatus(member, post.getTitle());
        saveMemberRoom(member, chatroom);
        chatroom.updateCurrentPeople();

        return ResponseEntity.status(HttpStatus.CREATED).body("모임에 참여하셨습니다.");
    }

    //모임 완료하기
    public ResponseEntity<?> leaveRoom(UserDetailsImpl userDetails, String roomCode) {
        Member member = userDetails.getMember();
        Long memberId = userDetails.getMember().getId();

        ChatRoom chatroom = chatRoomRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_CODE_NOT_FOUND));

        RoomParticipant roomParticipant = roomParticipantRepository.findByChatRoomIdAndMemberId(chatroom.getId(), memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_ROOM_NOT_FOUND));

        Post post = postRepository.findByChatRoomId(chatroom.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));


        Long postOwnerId = post.getOwner().getId();

        if (memberId.equals(postOwnerId)) {
            leavePostOwner(member, chatroom, roomParticipant, post);
        } else {
            leaveMember(member, chatroom, roomParticipant);
        }

        return ResponseEntity.status(HttpStatus.OK).body("모임을 완료하였습니다.");
    }


    // 참여한 유저 정보 리스트
    public List<JoinedMembersResponse> readMember(String roomCode) {
        ChatRoom chatroom = chatRoomRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_CODE_NOT_FOUND));

        return roomParticipantRepository.findJoinedMembersByMemberRoom(chatroom.getId());
    }

    private void ageAndGenderConditionCheck(Post post, int memberAge, GenderType memberGender) {
        int ageMinCondition = post.getAgeMin();
        int ageMaxCondition = post.getAgeMax();
        GenderType genderCondition = post.getGender();

        if (memberAge < ageMinCondition || memberAge > ageMaxCondition) {
            throw new CustomException(ErrorCode.AGE_CONDITION_NOT_MET);
        }

        if (!genderCondition.equals(GenderType.ALL) && !genderCondition.equals(memberGender)) {
            throw new CustomException(ErrorCode.GENDER_CONDITION_NOT_MET);
        }
    }

    private void checkIfAlreadyJoined(Member member) {
        if (member.getPostStatus() != null) {
            throw new CustomException(ErrorCode.PARTICIPATION_EXISTENCE);
        }
    }

    private void checkForDuplicateJoin(ChatRoom chatroom, Member member) {
        Optional<RoomParticipant> memberRoom = roomParticipantRepository.findByChatRoomAndMember(chatroom, member);

        if (memberRoom.isPresent()) {
            throw new CustomException(ErrorCode.ALREADY_JOINED_ROOM);
        }
    }

    private void checkFullNumOfPeople(ChatRoom chatroom) {
        if (chatroom.getMaxParticipant() <= chatroom.getCurrentParticipant()) {
            throw new CustomException(ErrorCode.ROOM_FULL_CAPACITY);
        }
    }

    private void leavePostOwner(Member member, ChatRoom chatroom, RoomParticipant roomParticipant, Post post) {
        updateStatus(member, chatroom, roomParticipant);
        postRepository.delete(post);
    }

    private void leaveMember(Member member, ChatRoom chatroom, RoomParticipant roomParticipant) {
        updateStatus(member, chatroom, roomParticipant);
    }

    private void updateStatus(Member member, ChatRoom chatroom, RoomParticipant roomParticipant) {
        roomParticipantRepository.deleteById(roomParticipant.getId());

        //참가자 state 값 변화.
        updatePostStatus(member, null);
        chatroom.minusCurrentPeople();
    }

    private void updatePostStatus(Member member, String post) {
        member.updatePostStatus(post);
        memberRepository.save(member);
    }

    private void saveMemberRoom(Member member, ChatRoom chatroom) {
        RoomParticipant roomParticipant = new RoomParticipant(member, chatroom);
        roomParticipantRepository.save(roomParticipant);
    }
}
