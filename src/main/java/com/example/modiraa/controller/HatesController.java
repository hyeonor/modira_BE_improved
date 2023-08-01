package com.example.modiraa.controller;

import com.example.modiraa.dto.request.LikesAndHatesUserIdDto;
import com.example.modiraa.service.HatesService;
import com.example.modiraa.auth.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class HatesController {
    private final HatesService hatesService;


    // 싫어요 기능
    @PostMapping("/api/hates")
    public ResponseEntity<?> userHates(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody LikesAndHatesUserIdDto userId) {

        return hatesService.userHates(userDetails, userId.getUserId());
    }

    // 싫어요 취소 기능
    @DeleteMapping("/api/hates")
    public ResponseEntity<?> deletetHates(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody LikesAndHatesUserIdDto userId)  {
        return hatesService.deleteHates(userDetails, userId.getUserId());
    }

}
