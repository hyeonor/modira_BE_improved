package com.example.modiraa.controller;

import com.example.modiraa.auth.UserDetailsImpl;
import com.example.modiraa.dto.request.PostRequest;
import com.example.modiraa.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/post")
public class PostController {

    private final PostService postService;

    // 모임 생성
    @PostMapping
    public ResponseEntity<String> createPost(@RequestBody PostRequest postRequest,
                                             @AuthenticationPrincipal UserDetailsImpl userDetails) {
        postService.createPost(postRequest, userDetails);
        return ResponseEntity.status(HttpStatus.CREATED).body("모임 생성 완료");
    }

    // 모임 삭제
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId,
                                           @AuthenticationPrincipal UserDetailsImpl userDetails) {
        postService.deletePost(postId, userDetails);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

}
