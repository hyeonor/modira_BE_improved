package com.example.modiraa.service;

import com.example.modiraa.auth.UserDetailsImpl;
import com.example.modiraa.dto.response.LoginCheckResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
    public LoginCheckResponse userInfo(UserDetailsImpl userDetails) {
        String username = userDetails.getUsername();
        String nickname = userDetails.getMember().getNickname();
        return new LoginCheckResponse(username, nickname);
    }
}