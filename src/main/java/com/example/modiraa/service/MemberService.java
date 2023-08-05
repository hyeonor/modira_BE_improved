package com.example.modiraa.service;

import com.example.modiraa.auth.UserDetailsImpl;
import com.example.modiraa.dto.response.LoginCheckResponse;
import com.example.modiraa.model.Member;
import com.example.modiraa.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    //로그인 유저 정보 반환
    public LoginCheckResponse userInfo(UserDetailsImpl userDetails) {
        String username = userDetails.getUsername();
        String nickname = userDetails.getMember().getNickname();
        return new LoginCheckResponse(username, nickname);
    }

    // 유저의 닉네임으로 유저 조회
    public Member getMember(String nickname) {
        return memberRepository.findByNickname(nickname).orElseThrow(() -> new IllegalArgumentException("회원이 아닙니다."));
    }
}