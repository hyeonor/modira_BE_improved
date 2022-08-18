package com.example.modiraa.post.controller;

import com.example.modiraa.loginAndRegister.auth.UserDetailsImpl;
import com.example.modiraa.post.dto.PostRequestDto;
import com.example.modiraa.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class PostController {

    private final PostService postService;

    // 모임 생성
    @PostMapping("/api/post")
    public ResponseEntity<String> createPost(@RequestBody PostRequestDto postRequestDto,
                                             @AuthenticationPrincipal UserDetailsImpl userDetails) {
        String username = userDetails.getUsername();
        postService.createPost(username, postRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("모임 생성 완료");
    }

    // 모임 삭제
    @DeleteMapping("/api/post/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId,
                                           @AuthenticationPrincipal UserDetailsImpl userDetails) {
        postService.deletePost(postId, userDetails);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

//    // 내가 작성한 모임 조회
//    @GetMapping("/api/myposts")
//    public ResponseEntity<PostsResponseDto> getMyReadPost(@AuthenticationPrincipal UserDetailsImpl userDetails) throws IllegalAccessException {
//
//        return ResponseEntity.status(HttpStatus.OK).body(postService.getMyReadPost(userDetails));
//    }

}
