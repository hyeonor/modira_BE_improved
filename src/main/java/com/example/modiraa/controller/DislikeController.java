package com.example.modiraa.controller;

import com.example.modiraa.auth.UserDetailsImpl;
import com.example.modiraa.dto.request.LikeAndDislikeRequest;
import com.example.modiraa.service.DislikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dislike")
public class DislikeController {
    private final DislikeService dislikeService;

    // 싫어요 기능
    @PostMapping
    public ResponseEntity<String> rateDislike(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                              @RequestBody LikeAndDislikeRequest request) {
        return dislikeService.rateDislike(userDetails, request.getUserId());
    }

    // 싫어요 취소 기능
    @DeleteMapping
    public ResponseEntity<String> deleteDislike(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                @RequestBody LikeAndDislikeRequest request) {
        return dislikeService.deleteDislike(userDetails, request.getUserId());
    }

}
