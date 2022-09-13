package com.example.modiraa.service;

import com.example.modiraa.auth.UserDetailsImpl;
import com.example.modiraa.dto.JoinUserListResponseDto;
import com.example.modiraa.exception.CustomException;
import com.example.modiraa.exception.ErrorCode;
import com.example.modiraa.model.ChatRoom;
import com.example.modiraa.model.Member;
import com.example.modiraa.model.MemberRoom;
import com.example.modiraa.model.Post;
import com.example.modiraa.repository.ChatRoomRepository;
import com.example.modiraa.repository.MemberRoomRepository;
import com.example.modiraa.repository.PostRepository;
import com.example.modiraa.repository.UserRepository;
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

    private final MemberRoomRepository memberRoomRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    //채팅참여하기
    public ResponseEntity<?> enterRoom(UserDetailsImpl userDetails, String roomId) {
        Member member = userDetails.getMember();
        Optional<ChatRoom> chatroom = chatRoomRepository.findByRoomId(roomId);
        Optional<MemberRoom> memberRoom1 = memberRoomRepository.findByChatRoomAndMember(chatroom,member);
        if (chatroom.isEmpty()){
            throw new CustomException(ErrorCode.JOIN_ROOM_CHECK_CODE);
        }
        Post postRoom = postRepository.findByChatRoomId(chatroom.get().getId());

        //post 성별 조건
        String postGender = postRoom.getGender();
        //member의 성별
        String memberGender = member.getGender();
        //post 나이 조건
        String age = postRoom.getAge();
        //member의 나이
        String memberAge = userDetails.getMember().getAge();

        int ageMin = Integer.parseInt(age.split("~")[0].split("대")[0]);
        int ageMax = Integer.parseInt(age.split("~")[1].split("대")[0]);
        int memberAgeInt = Integer.parseInt(memberAge.split("~")[0].split("대")[0]);
        int setAge = 0;

        //성별 조건
        if (postGender.equals("모든성별")){
            return joinRoom(member, chatroom, memberRoom1, postRoom);
        }else if (postGender.equals(memberGender)) {
            return joinRoom(member, chatroom, memberRoom1, postRoom);
        }
        if (!postGender.equals(memberGender)){
            throw new CustomException(ErrorCode.JOIN_GENDER_CHECK_CODE);
        }
        //나이 조건
        if (age.equals("모든나이")) {
            return joinRoom(member, chatroom, memberRoom1, postRoom);
        }
        //나이 재 정렬
        else if (ageMin > ageMax) {
            setAge = ageMin;
            ageMin = ageMax;
            ageMax = setAge;
        }
        if (memberAgeInt <= ageMax && memberAgeInt >= ageMin){
            return joinRoom(member, chatroom, memberRoom1, postRoom);
        }else {
            throw new CustomException(ErrorCode.JOIN_AGE_CHECK_CODE);
        }
    }


    //모임 완료하기
    public ResponseEntity<?> leaveRoom(UserDetailsImpl userDetails, String roomId) {
        Member member = userDetails.getMember();
        Optional<ChatRoom> chatroom = chatRoomRepository.findByRoomId(roomId);

        if (chatroom.isEmpty()){
            throw new CustomException(ErrorCode.JOIN_ROOM_CHECK_CODE);
        }

        Long chatroomId = chatroom.get().getId();

        MemberRoom memberRoom  = memberRoomRepository.findByChatRoomIdAndMember_Id(chatroomId, member.getId()).orElseThrow(
                () -> new CustomException(ErrorCode.JOIN_ROOM_CHECK_CODE)
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

    private void stateUpdate(Member member, Optional<ChatRoom> chatroom, Long memberRoomId) {
        memberRoomRepository.deleteById(memberRoomId);

        //참가자 state 값 변화.
        member.setPostState(null);
        userRepository.save(member);
        chatroom.get().minusCurrentPeople();
    }

    // 참여한 유저 정보 리스트
    public List<JoinUserListResponseDto> ReadMember(String roomId) {
        Optional<ChatRoom> chatroom = chatRoomRepository.findByRoomId(roomId);
        if (chatroom.isEmpty()){
            throw new IllegalArgumentException("존재하지 않는 모임 입니다.");
        }
        return memberRoomRepository.RoomUserList(chatroom.get());
    }


    //룸 참가
    private ResponseEntity<String> joinRoom(Member member, Optional<ChatRoom> chatroom, Optional<MemberRoom> memberRoom1, Post postRoom) {
        if(member.getPostState() == null) {
            if (chatroom.get().getMaxPeople() > chatroom.get().getCurrentPeople()) {
                MemberRoom memberRoom = new MemberRoom(member, chatroom.get());
                memberRoomRepository.save(memberRoom);
                chatroom.get().updateCurrentPeople();
            } else {
                throw new CustomException(ErrorCode.JOIN_PULL_CHECK_CODE);
            }
            if (memberRoom1.isPresent()) {
                throw new CustomException(ErrorCode.JOIN_CHECK_CODE);
            }

            //참가자 state 값 변화.
            member.setPostState(postRoom.getTitle());
            userRepository.save(member);
        } else {
            throw new CustomException(ErrorCode.JOIN_CHATROOM_CHECK_CODE);
        }

        return new ResponseEntity<>("모임에 참여하셨습니다.", HttpStatus.valueOf(200));
    }


}
