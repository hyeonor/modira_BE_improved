package com.example.modiraa.service;

import com.example.modiraa.auth.UserDetailsImpl;
import com.example.modiraa.dto.response.JoinedMembersResponse;
import com.example.modiraa.exception.CustomException;
import com.example.modiraa.exception.ErrorCode;
import com.example.modiraa.model.ChatRoom;
import com.example.modiraa.model.Member;
import com.example.modiraa.model.MemberRoom;
import com.example.modiraa.model.Post;
import com.example.modiraa.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class MemberRoomService {

    private final MemberRoomQueryRepository memberRoomQueryRepository;
    private final MemberRoomRepository memberRoomRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    //채팅 참여하기
    public ResponseEntity<?> enterRoom(UserDetailsImpl userDetails, String roomId) {
        Member member = userDetails.getMember();
        ChatRoom chatroom = chatRoomRepository.findByRoomId(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.JOIN_ROOM_CHECK_CODE));
        Optional<MemberRoom> memberRoom = memberRoomRepository.findByChatRoomAndMember(chatroom,member);
        Post postRoom = postRepository.findByChatRoomId(chatroom.getId());

        //post 성별 조건
        String postGender = postRoom.getGender();
        //member의 성별
        String memberGender = member.getGender();
        //post 나이 조건
        String age = postRoom.getAge();
        //member의 나이
        String memberAge = userDetails.getMember().getAge();


        if(member.getPostState() == null) {
            if (chatroom.getMaxPeople() > chatroom.getCurrentPeople()) {
                MemberRoom SaveMemberRoom = new MemberRoom(member, chatroom);
                memberRoomRepository.save(SaveMemberRoom);
                chatroom.updateCurrentPeople();
            } else {
                throw new CustomException(ErrorCode.JOIN_PULL_CHECK_CODE);
            }
            if (memberRoom.isPresent()) {
                throw new CustomException(ErrorCode.JOIN_CHECK_CODE);
            }
            //-----------------------------------------------------------------
            if (age.equals("모든나이")) {
                return genderCheck(member, postRoom, postGender, memberGender);
            }

            int ageOne = Integer.parseInt(age.split("~")[0].split("대")[0]);
            int ageTwo = Integer.parseInt(age.split("~")[1].split("대")[0]);
            int memberAgeInt = Integer.parseInt(memberAge.split("~")[0].split("대")[0]);
            int ageMax = Math.max(ageOne, ageTwo);
            int ageMin = Math.min(ageOne, ageTwo);

            //나이 순서 정렬
            if (memberAgeInt <= ageMax && memberAgeInt >= ageMin){
                return genderCheck(member, postRoom, postGender, memberGender);
            }else {
                throw new CustomException(ErrorCode.JOIN_AGE_CHECK_CODE);
            }
        } else {
            throw new CustomException(ErrorCode.JOIN_CHATROOM_CHECK_CODE);
        }
    }

    private ResponseEntity<String> genderCheck(Member member, Post postRoom, String postGender, String memberGender) {
        if(postGender.equals("모든성별")){
            //참가자 state 값 변화.
            member.setPostState(postRoom.getTitle());
            memberRepository.save(member);
            return new ResponseEntity<>("모임에 참여하셨습니다.", HttpStatus.valueOf(200));
        }else if (postGender.equals(memberGender)) {
            member.setPostState(postRoom.getTitle());
            memberRepository.save(member);
            return new ResponseEntity<>("모임에 참여하셨습니다.", HttpStatus.valueOf(200));
        }else {
            throw new CustomException(ErrorCode.JOIN_GENDER_CHECK_CODE);
        }
    }


    //모임 완료하기
    public ResponseEntity<?> leaveRoom(UserDetailsImpl userDetails, String roomId) {
        Member member = userDetails.getMember();
        ChatRoom chatroom = chatRoomRepository.findByRoomId(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.JOIN_ROOM_CHECK_CODE));

        Long chatroomId = chatroom.getId();

        MemberRoom memberRoom  = memberRoomQueryRepository.findByChatRoomIdAndMemberId(chatroomId, member.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.JOIN_ROOM_CHECK_CODE)
        );

        Long memberRoomId = memberRoom.getId();
        Long memberId = userDetails.getMember().getId();

        Post post = postRepository.findByChatRoomId(chatroomId);

        if (post != null) {
            if (memberId.equals(post.getMember().getId())) {
                stateUpdate(member, chatroom, memberRoomId);
                postRepository.delete(post);
            }
            else {
                stateUpdate(member, chatroom, memberRoomId);
            }
        } else {
            stateUpdate(member, chatroom, memberRoomId);
        }

        return new ResponseEntity<>("모임을 완료하였습니다.", HttpStatus.valueOf(200));
    }

    private void stateUpdate(Member member, ChatRoom chatroom, Long memberRoomId) {
        memberRoomRepository.deleteById(memberRoomId);

        //참가자 state 값 변화.
        member.setPostState(null);
        memberRepository.save(member);
        chatroom.minusCurrentPeople();
    }

    // 참여한 유저 정보 리스트
    public List<JoinedMembersResponse> ReadMember(String roomId) {
        ChatRoom chatroom = chatRoomRepository.findByRoomId(roomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 모임 입니다."));

        return memberRoomQueryRepository.findJoinedMembersByMemberRoom(chatroom.getId());
    }
}
