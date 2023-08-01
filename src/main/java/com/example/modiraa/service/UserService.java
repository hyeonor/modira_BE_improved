package com.example.modiraa.service;

import com.example.modiraa.auth.UserDetailsImpl;
import com.example.modiraa.dto.response.LoginIdCheckDto;
import com.example.modiraa.model.Member;
import com.example.modiraa.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final MemberRepository memberRepository;

    //로그인 유저 정보 반환
    public LoginIdCheckDto userInfo(UserDetailsImpl userDetails) {
        String username = userDetails.getUsername();
        String usernickname = userDetails.getMember().getNickname();
        LoginIdCheckDto userinfo = new LoginIdCheckDto(username, usernickname);
        return userinfo;
    }

    // 유저의 닉네임으로 유저 조회
    public Member getMember(String nickname) {
        return memberRepository.findByNickname(nickname).orElseThrow(() -> new IllegalArgumentException("회원이 아닙니다."));
    }
}