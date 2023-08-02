package com.example.modiraa.controller;

import com.example.modiraa.auth.UserDetailsImpl;
import com.example.modiraa.dto.request.LikesAndHatesUserIdDto;
import com.example.modiraa.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class LikesController {

    private final LikeService likeService;

    // 좋아요 기능
    @PostMapping("/api/likes")
    public ResponseEntity<?> userLikes(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody LikesAndHatesUserIdDto userId) {
        return likeService.userLikes(userDetails, userId.getUserId());
    }

    // 좋아요 취소 기능
    @DeleteMapping("/api/likes")
    public ResponseEntity<?> deletetLikes(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody LikesAndHatesUserIdDto userId) {
        return likeService.deleteLikes(userDetails, userId.getUserId());
    }

}