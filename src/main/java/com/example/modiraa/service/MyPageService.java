package com.example.modiraa.service;

import com.example.modiraa.dto.MyUserProfileResponseDto;
import com.example.modiraa.model.ChatRoom;
import com.example.modiraa.model.MemberRoom;
import com.example.modiraa.model.Post;
import com.example.modiraa.repository.*;
import com.example.modiraa.auth.UserDetailsImpl;
import com.example.modiraa.model.Member;
import com.example.modiraa.dto.UserProfileResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class MyPageService {

    private final UserRepository userRepository;
    private final LikesRepository likesRepository;
    private final HatesRepository hatesRepository;
    private final MemberRoomRepository memberRoomRepository;

// 유저 프로필 조회
    public UserProfileResponseDto getProfile(Long id) throws IllegalAccessException {

        Member member = userRepository.findById(id)
                .orElseThrow(()-> new IllegalAccessException("유저의 정보가 없습니다."));

        Long score = likesRepository.likesCount(member) - hatesRepository.hatesCount(member);

        return UserProfileResponseDto.builder()
                .address(member.getAddress())
                .age(member.getAge())
                .userProfile(member.getProfileImage())
                .gender(member.getGender())
                .nickname(member.getNickname())
                .score(score)
                .build();


    }

    //마이프로필 조회
    public MyUserProfileResponseDto getMyProfileRead(UserDetailsImpl userDetails) {

        Member member = userDetails.getMember();
        Long score = likesRepository.likesCount(member) - hatesRepository.hatesCount(member);

        String roomId = null;

        Optional<MemberRoom> memberRoom = memberRoomRepository.findTopByMemberOrderByIdDesc(member);
        if(memberRoom.isPresent()) {
             roomId = memberRoom.get().getChatRoom().getRoomId();
        }

        return MyUserProfileResponseDto.builder()
                .address(member.getAddress())
                .age(member.getAge())
                .userProfile(member.getProfileImage())
                .gender(member.getGender())
                .nickname(member.getNickname())
                .score(score)
                .isJoinPost(member.getPostState())
                .roomId(roomId)
                .build();
    }
}
