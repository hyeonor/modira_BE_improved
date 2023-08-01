package com.example.modiraa.controller;

import com.example.modiraa.dto.request.LikesAndHatesUserIdDto;
import com.example.modiraa.service.LikesService;
import com.example.modiraa.auth.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class LikesController {

    private final LikesService likesService;

    // 좋아요 기능
    @PostMapping("/api/likes")
    public ResponseEntity<?> userLikes(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody LikesAndHatesUserIdDto userId) {
        return likesService.userLikes(userDetails, userId.getUserId());
    }

    // 좋아요 취소 기능
    @DeleteMapping("/api/likes")
    public ResponseEntity<?> deletetLikes(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody LikesAndHatesUserIdDto userId)  {
        return likesService.deleteLikes(userDetails, userId.getUserId());
    }

}