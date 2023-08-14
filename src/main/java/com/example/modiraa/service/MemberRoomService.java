package com.example.modiraa.service;

import com.example.modiraa.auth.UserDetailsImpl;
import com.example.modiraa.dto.response.JoinedMembersResponse;
import com.example.modiraa.enums.GenderType;
import com.example.modiraa.exception.CustomException;
import com.example.modiraa.exception.ErrorCode;
import com.example.modiraa.model.ChatRoom;
import com.example.modiraa.model.Member;
import com.example.modiraa.model.MemberRoom;
import com.example.modiraa.model.Post;
import com.example.modiraa.repository.ChatRoomRepository;
import com.example.modiraa.repository.MemberRepository;
import com.example.modiraa.repository.MemberRoomRepository;
import com.example.modiraa.repository.PostRepository;
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
public class MemberRoomService {
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MemberRoomRepository memberRoomRepository;

    //채팅 참여하기
    public ResponseEntity<?> enterRoom(UserDetailsImpl userDetails, String roomCode) {
        Member member = userDetails.getMember();
        ChatRoom chatroom = chatRoomRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new CustomException(ErrorCode.JOIN_ROOM_CHECK_CODE));
        Post post = postRepository.findByChatRoomId(chatroom.getId())
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

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
                .orElseThrow(() -> new CustomException(ErrorCode.JOIN_ROOM_CHECK_CODE));

        MemberRoom memberRoom = memberRoomRepository.findByChatRoomIdAndMemberId(chatroom.getId(), memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.JOIN_ROOM_CHECK_CODE));

        Post post = postRepository.findByChatRoomId(chatroom.getId())
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));


        Long postOwnerId = post.getMember().getId();

        if (memberId.equals(postOwnerId)) {
            leavePostOwner(member, chatroom, memberRoom, post);
        } else {
            leaveMember(member, chatroom, memberRoom);
        }

        return ResponseEntity.status(HttpStatus.OK).body("모임을 완료하였습니다.");
    }


    // 참여한 유저 정보 리스트
    public List<JoinedMembersResponse> readMember(String roomCode) {
        ChatRoom chatroom = chatRoomRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 모임 입니다."));

        return memberRoomRepository.findJoinedMembersByMemberRoom(chatroom.getId());
    }

    private void ageAndGenderConditionCheck(Post post, int memberAge, GenderType memberGender) {
        int ageMinCondition = post.getAgeMin();
        int ageMaxCondition = post.getAgeMax();
        GenderType genderCondition = post.getGender();

        if (memberAge < ageMinCondition || memberAge > ageMaxCondition) {
            throw new CustomException(ErrorCode.JOIN_AGE_CHECK_CODE);
        }

        if (!genderCondition.equals(GenderType.ALL) && !genderCondition.equals(memberGender)) {
            throw new CustomException(ErrorCode.JOIN_GENDER_CHECK_CODE);
        }
    }

    private void checkIfAlreadyJoined(Member member) {
        if (member.getPostStatus() != null) {
            throw new CustomException(ErrorCode.JOIN_CHATROOM_CHECK_CODE);
        }
    }

    private void checkForDuplicateJoin(ChatRoom chatroom, Member member) {
        Optional<MemberRoom> memberRoom = memberRoomRepository.findByChatRoomAndMember(chatroom, member);

        if (memberRoom.isPresent()) {
            throw new CustomException(ErrorCode.JOIN_CHECK_CODE);
        }
    }

    private void checkFullNumOfPeople(ChatRoom chatroom) {
        if (chatroom.getMaxPeople() <= chatroom.getCurrentPeople()) {
            throw new CustomException(ErrorCode.JOIN_PULL_CHECK_CODE);
        }
    }

    private void leavePostOwner(Member member, ChatRoom chatroom, MemberRoom memberRoom, Post post) {
        updateStatus(member, chatroom, memberRoom);
        postRepository.delete(post);
    }

    private void leaveMember(Member member, ChatRoom chatroom, MemberRoom memberRoom) {
        updateStatus(member, chatroom, memberRoom);
    }

    private void updateStatus(Member member, ChatRoom chatroom, MemberRoom memberRoom) {
        memberRoomRepository.deleteById(memberRoom.getId());

        //참가자 state 값 변화.
        updatePostStatus(member, null);
        chatroom.minusCurrentPeople();
    }

    private void updatePostStatus(Member member, String post) {
        member.updatePostStatus(post);
        memberRepository.save(member);
    }

    private void saveMemberRoom(Member member, ChatRoom chatroom) {
        MemberRoom memberRoom = new MemberRoom(member, chatroom);
        memberRoomRepository.save(memberRoom);
    }
}
