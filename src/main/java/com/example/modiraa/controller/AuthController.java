package com.example.modiraa.controller;

import com.example.modiraa.dto.request.AdditionalInfoRequest;
import com.example.modiraa.dto.request.oauth.KakaoLoginParams;
import com.example.modiraa.dto.request.oauth.NaverLoginParams;
import com.example.modiraa.dto.response.SocialResponse;
import com.example.modiraa.service.OAuthLoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final OAuthLoginService oAuthLoginService;

    // 소셜 회원 가입 요청 처리
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@ModelAttribute AdditionalInfoRequest request) throws IOException {
        oAuthLoginService.createNewMember(request);
        return ResponseEntity.ok("회원가입 완료");
    }

    @PostMapping("/kakao")
    public ResponseEntity<SocialResponse> loginKakao(@RequestBody KakaoLoginParams params) {
        return oAuthLoginService.login(params);
    }

    @PostMapping("/naver")
    public ResponseEntity<SocialResponse> loginNaver(@RequestBody NaverLoginParams params) {
        return oAuthLoginService.login(params);
    }
}
