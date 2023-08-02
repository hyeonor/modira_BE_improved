package com.example.modiraa.service;

import com.example.modiraa.auth.UserDetailsImpl;
import com.example.modiraa.dto.response.MyUserProfileResponseDto;
import com.example.modiraa.dto.response.UserProfileResponseDto;
import com.example.modiraa.model.Member;
import com.example.modiraa.model.MemberRoom;
import com.example.modiraa.repository.DislikeRepository;
import com.example.modiraa.repository.LikeRepository;
import com.example.modiraa.repository.MemberRepository;
import com.example.modiraa.repository.MemberRoomQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class MyPageService {
    private final LikeRepository likeRepository;
    private final DislikeRepository dislikeRepository;
    private final MemberRepository memberRepository;
    private final MemberRoomQueryRepository memberRoomQueryRepository;

    // 유저 프로필 조회
    public UserProfileResponseDto getProfile(Long id) throws IllegalAccessException {

        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalAccessException("유저의 정보가 없습니다."));

        Long score = likeRepository.likesCount(member) - dislikeRepository.hatesCount(member);

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
        Long score = likeRepository.likesCount(member) - dislikeRepository.hatesCount(member);

        String roomId = null;

        Optional<MemberRoom> memberRoom = memberRoomQueryRepository.findTopByMemberOrderByIdDesc(member);
        if (memberRoom.isPresent()) {
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
