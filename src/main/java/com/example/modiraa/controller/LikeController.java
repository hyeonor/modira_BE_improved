package com.example.modiraa.controller;

import com.example.modiraa.auth.UserDetailsImpl;
import com.example.modiraa.dto.request.LikeAndDislikeRequest;
import com.example.modiraa.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/likes")
public class LikeController {
    private final LikeService likeService;

    // 좋아요 기능
    @PostMapping
    public ResponseEntity<String> rateLike(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                           @RequestBody LikeAndDislikeRequest request) {
        return likeService.rateLike(userDetails, request.getUserId());
    }

    // 좋아요 취소 기능
    @DeleteMapping
    public ResponseEntity<String> deleteLike(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                             @RequestBody LikeAndDislikeRequest request) {
        return likeService.deleteLike(userDetails, request.getUserId());
    }

}