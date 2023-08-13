package com.example.modiraa.controller;

import com.example.modiraa.auth.UserDetailsImpl;
import com.example.modiraa.dto.request.RatingRequest;
import com.example.modiraa.service.RatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member/ratings")
public class RatingController {
    private final RatingService ratingService;

    // 평가 기능
    @PostMapping
    public ResponseEntity<String> rate(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                       @RequestBody RatingRequest request) {
        return ratingService.rate(userDetails, request.getUserId(), request.getRatingType());
    }

    // 평가 취소 기능
    @DeleteMapping
    public ResponseEntity<String> deleteRating(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                               @RequestBody RatingRequest request) {
        return ratingService.deleteRating(userDetails, request.getUserId(), request.getRatingType());
    }
}
