package com.example.modiraa.mypage.controller;

import com.example.modiraa.loginAndRegister.auth.UserDetailsImpl;
import com.example.modiraa.mypage.dto.UserProfileResponseDto;
import com.example.modiraa.mypage.service.MyPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class MyPageController {

    private final MyPageService myPageService;

    // 다른유저 프로필 조회
    @GetMapping("/api/user/info/{id}")
    public ResponseEntity<UserProfileResponseDto> profileRead(@PathVariable Long id) throws IllegalAccessException {

        return ResponseEntity.status(HttpStatus.OK)
                .body(myPageService.getProfile(id));

    }

    // 마이프로필 조회
    @GetMapping("/api/user/info")
    public ResponseEntity<UserProfileResponseDto> getMyProfileRead(@AuthenticationPrincipal UserDetailsImpl userDetails) throws IllegalAccessException {

        return ResponseEntity.status(HttpStatus.OK)
                .body(myPageService.getMyProfileRead(userDetails));

    }

}