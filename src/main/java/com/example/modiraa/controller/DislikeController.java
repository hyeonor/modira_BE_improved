package com.example.modiraa.controller;

import com.example.modiraa.auth.UserDetailsImpl;
import com.example.modiraa.dto.request.LikeAndDislikeRequestDto;
import com.example.modiraa.service.DislikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class DislikeController {
    private final DislikeService dislikeService;

    // 싫어요 기능
    @PostMapping("/api/hates")
    public ResponseEntity<?> userHates(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody LikeAndDislikeRequestDto userId) {
        return dislikeService.userHates(userDetails, userId.getUserId());
    }

    // 싫어요 취소 기능
    @DeleteMapping("/api/hates")
    public ResponseEntity<?> deletetHates(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody LikeAndDislikeRequestDto userId) {
        return dislikeService.deleteHates(userDetails, userId.getUserId());
    }

}
