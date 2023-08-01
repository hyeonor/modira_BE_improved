package com.example.modiraa.controller;

import com.example.modiraa.dto.SocialResponseDto;
import com.example.modiraa.model.KakaoLoginParams;
import com.example.modiraa.model.NaverLoginParams;
import com.example.modiraa.service.OAuthLoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final OAuthLoginService oAuthLoginService;

    @PostMapping("/kakao")
    public ResponseEntity<SocialResponseDto> loginKakao(@RequestBody KakaoLoginParams params) {
        return oAuthLoginService.login(params);
    }

    @PostMapping("/naver")
    public ResponseEntity<SocialResponseDto> loginNaver(@RequestBody NaverLoginParams params) {
        return oAuthLoginService.login(params);
    }
}
