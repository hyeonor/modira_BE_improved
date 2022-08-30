package com.example.modiraa.controller;

import com.example.modiraa.repository.ChatRoomRepository;
import com.example.modiraa.service.PostService;
import com.example.modiraa.dto.PostRequestDto;
import com.example.modiraa.auth.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class PostController {

    private final PostService postService;
    private final ChatRoomRepository chatRoomRepository;

    // 모임 생성
    @PostMapping("/api/post")
    public ResponseEntity<String> createPost(@RequestBody PostRequestDto postRequestDto,
                                             @AuthenticationPrincipal UserDetailsImpl userDetails) {
        String username = userDetails.getUsername();
        chatRoomRepository.createChatRoom();
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

}
