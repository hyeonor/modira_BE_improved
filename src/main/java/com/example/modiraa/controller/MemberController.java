package com.example.modiraa.controller;

import com.example.modiraa.auth.UserDetailsImpl;
import com.example.modiraa.dto.response.LoginCheckResponse;
import com.example.modiraa.service.MemberService;
import com.example.modiraa.service.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MemberController {
    private final S3Uploader s3Uploader;
    private final MemberService memberService;

    //S3 Test controller
    @PostMapping("/upload")
    @ResponseBody
    public String upload(@RequestParam("data") MultipartFile multipartFile) throws IOException {
        return s3Uploader.upload(multipartFile, "static");
    }

    //로그인 유저 정보
    @GetMapping("/login/auth")
    public LoginCheckResponse userDetails(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return memberService.userInfo(userDetails);
    }
}