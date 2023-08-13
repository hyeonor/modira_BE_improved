package com.example.modiraa.service;

import com.example.modiraa.auth.UserDetailsImpl;
import com.example.modiraa.dto.response.MyProfileResponse;
import com.example.modiraa.dto.response.UserProfileResponse;
import com.example.modiraa.model.Member;
import com.example.modiraa.model.MemberRoom;
import com.example.modiraa.repository.MemberRepository;
import com.example.modiraa.repository.MemberRoomQueryRepository;
import com.example.modiraa.repository.RatingQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberProfileService {
    private final MemberRepository memberRepository;
    private final RatingQueryRepository ratingQueryRepository;
    private final MemberRoomQueryRepository memberRoomQueryRepository;

    // 마이프로필 조회
    public MyProfileResponse getMyProfile(UserDetailsImpl userDetails) {
        Member member = userDetails.getMember();
        Long score = ratingQueryRepository.calculateScore(member.getId());

        Optional<MemberRoom> memberRoom = memberRoomQueryRepository.findTopByMemberOrderByIdDesc(member);
        String roomCode = memberRoom.map(mr -> mr.getChatRoom().getRoomCode()).orElse(null);

        if (memberRoom.isPresent()) {
            roomCode = memberRoom.get().getChatRoom().getRoomCode();
        }

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
    public UserProfileResponse getProfile(Long id) throws IllegalAccessException {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalAccessException("유저의 정보가 없습니다."));

        Long score = ratingQueryRepository.calculateScore(member.getId());

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
