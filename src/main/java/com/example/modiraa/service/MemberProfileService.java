package com.example.modiraa.service;

import com.example.modiraa.auth.UserDetailsImpl;
import com.example.modiraa.dto.response.MyProfileResponse;
import com.example.modiraa.dto.response.UserProfileResponse;
import com.example.modiraa.model.Member;
import com.example.modiraa.model.MemberRoom;
import com.example.modiraa.repository.LikeDislikeQueryRepository;
import com.example.modiraa.repository.MemberRepository;
import com.example.modiraa.repository.MemberRoomQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class MemberProfileService {
    private final MemberRepository memberRepository;
    private final MemberRoomQueryRepository memberRoomQueryRepository;
    private final LikeDislikeQueryRepository likeDislikeQueryRepository;

    // 마이프로필 조회
    public MyProfileResponse getMyProfile(UserDetailsImpl userDetails) {
        Member member = userDetails.getMember();
        Long score = likeDislikeQueryRepository.calculateScore(member);

        Optional<MemberRoom> memberRoom = memberRoomQueryRepository.findTopByMemberOrderByIdDesc(member);
        String roomId = memberRoom.map(mr -> mr.getChatRoom().getRoomId()).orElse(null);

        if (memberRoom.isPresent()) {
            roomId = memberRoom.get().getChatRoom().getRoomId();
        }

        return MyProfileResponse.builder()
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

    // 유저 프로필 조회
    public UserProfileResponse getProfile(Long id) throws IllegalAccessException {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalAccessException("유저의 정보가 없습니다."));

        Long score = likeDislikeQueryRepository.calculateScore(member);

        return UserProfileResponse.builder()
                .address(member.getAddress())
                .age(member.getAge())
                .userProfile(member.getProfileImage())
                .gender(member.getGender())
                .nickname(member.getNickname())
                .score(score)
                .build();
    }
}
