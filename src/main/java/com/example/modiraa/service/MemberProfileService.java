package com.example.modiraa.service;

import com.example.modiraa.auth.UserDetailsImpl;
import com.example.modiraa.dto.response.MyProfileResponse;
import com.example.modiraa.dto.response.UserProfileResponse;
import com.example.modiraa.exception.CustomException;
import com.example.modiraa.exception.ErrorCode;
import com.example.modiraa.model.Member;
import com.example.modiraa.model.RoomParticipant;
import com.example.modiraa.repository.MemberRepository;
import com.example.modiraa.repository.RatingRepository;
import com.example.modiraa.repository.RoomParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberProfileService {
    private final MemberRepository memberRepository;
    private final RatingRepository ratingRepository;
    private final RoomParticipantRepository roomParticipantRepository;

    // 마이프로필 조회
    public MyProfileResponse getMyProfile(UserDetailsImpl userDetails) {
        Member member = userDetails.getMember();
        Long score = ratingRepository.calculateScore(member.getId());

        Optional<RoomParticipant> memberRoom = roomParticipantRepository.findTopByMemberOrderByIdDesc(member);
        String roomCode = memberRoom.map(mr -> mr.getChatRoom().getRoomCode()).orElse(null);

        return MyProfileResponse.builder()
                .address(member.getAddress())
                .age(member.getAge())
                .userProfile(member.getProfileImage())
                .gender(member.getGender().getValue())
                .nickname(member.getNickname())
                .score(score)
                .isJoinPost(member.getPostStatus())
                .roomCode(roomCode)
                .build();
    }

    // 유저 프로필 조회
    public UserProfileResponse getProfile(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Long score = ratingRepository.calculateScore(member.getId());

        return UserProfileResponse.builder()
                .address(member.getAddress())
                .age(member.getAge())
                .userProfile(member.getProfileImage())
                .gender(member.getGender().getValue())
                .nickname(member.getNickname())
                .score(score)
                .build();
    }
}
