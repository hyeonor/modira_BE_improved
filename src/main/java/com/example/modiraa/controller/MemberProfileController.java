package com.example.modiraa.controller;

import com.example.modiraa.auth.UserDetailsImpl;
import com.example.modiraa.dto.response.MyProfileResponse;
import com.example.modiraa.dto.response.UserProfileResponse;
import com.example.modiraa.service.MemberProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user/info")
public class MemberProfileController {

    private final MemberProfileService memberProfileService;

    // 마이프로필 조회
    @GetMapping
    public ResponseEntity<MyProfileResponse> getMyProfile(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(memberProfileService.getMyProfile(userDetails));
    }

    // 다른 유저 프로필 조회
    @GetMapping("/{id}")
    public ResponseEntity<UserProfileResponse> profileRead(@PathVariable Long id) throws IllegalAccessException {
        return ResponseEntity.status(HttpStatus.OK)
                .body(memberProfileService.getProfile(id));
    }

}