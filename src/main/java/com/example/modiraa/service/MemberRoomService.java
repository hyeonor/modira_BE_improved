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
import com.example.modiraa.repository.*;
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
    private final MemberRoomQueryRepository memberRoomQueryRepository;

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

        GenderType genderCondition = post.getGender();
        String ageCondition = post.getAge();
        GenderType memberGender = member.getGender();
        String memberAge = member.getAge();

        if (ageCondition.equals("모든나이")) {
            return genderConditionCheck(member, chatroom, post, genderCondition, memberGender);
        }

        int ageOne = Integer.parseInt(ageCondition.split("~")[0].split("대")[0]);
        int ageTwo = Integer.parseInt(ageCondition.split("~")[1].split("대")[0]);
        int memberAgeInt = Integer.parseInt(memberAge.split("대")[0]);
        int ageMax = Math.max(ageOne, ageTwo);
        int ageMin = Math.min(ageOne, ageTwo);


        if (memberAgeInt <= ageMax && memberAgeInt >= ageMin) {
            return genderConditionCheck(member, chatroom, post, genderCondition, memberGender);
        } else {
            throw new CustomException(ErrorCode.JOIN_AGE_CHECK_CODE);
        }
    }

    //모임 완료하기
    public ResponseEntity<?> leaveRoom(UserDetailsImpl userDetails, String roomCode) {
        Member member = userDetails.getMember();
        Long memberId = userDetails.getMember().getId();

        ChatRoom chatroom = chatRoomRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new CustomException(ErrorCode.JOIN_ROOM_CHECK_CODE));

        MemberRoom memberRoom = memberRoomQueryRepository.findByChatRoomIdAndMemberId(chatroom.getId(), memberId)
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

        return memberRoomQueryRepository.findJoinedMembersByMemberRoom(chatroom.getId());
    }

    private ResponseEntity<String> genderConditionCheck(Member member, ChatRoom chatroom, Post postRoom,
                                                        GenderType genderCondition, GenderType memberGender) {
        if (genderCondition.equals(GenderType.ALL) || genderCondition.equals(memberGender)) {
            member.updatePostStatus(postRoom.getTitle());
            memberRepository.save(member);

            MemberRoom saveMemberRoom = new MemberRoom(member, chatroom);
            memberRoomRepository.save(saveMemberRoom);
            chatroom.updateCurrentPeople();

            return ResponseEntity.status(HttpStatus.CREATED).body("모임에 참여하셨습니다.");
        } else {
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
        member.updatePostStatus(null);
        memberRepository.save(member);
        chatroom.minusCurrentPeople();
    }

}
