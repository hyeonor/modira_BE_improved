package com.example.modiraa.controller;

import com.example.modiraa.auth.UserDetailsImpl;
import com.example.modiraa.dto.request.AdditionalInfoRequest;
import com.example.modiraa.dto.response.LoginIdCheckDto;
import com.example.modiraa.service.OAuthLoginService;
import com.example.modiraa.service.S3Uploader;
import com.example.modiraa.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MemberController {

    private final S3Uploader s3Uploader;
    private final UserService userService;
    private final OAuthLoginService oAuthLoginService;

    //S3 Test controller
    @PostMapping("/upload")
    @ResponseBody
    public String upload(@RequestParam("data") MultipartFile multipartFile) throws IOException {
        return s3Uploader.upload(multipartFile, "static");
    }

    // 소셜 회원 가입 요청 처리
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@ModelAttribute AdditionalInfoRequest request) throws IOException {
        oAuthLoginService.createNewMember(request);
        return ResponseEntity.ok("회원가입 완료");
    }

    //로그인 유저 정보
    @GetMapping("/login/auth")
    public LoginIdCheckDto userDetails(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return userService.userInfo(userDetails);
    }
}