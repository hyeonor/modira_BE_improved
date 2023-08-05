package com.example.modiraa.controller;

import com.example.modiraa.auth.UserDetailsImpl;
import com.example.modiraa.dto.response.*;
import com.example.modiraa.service.PostReadService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/post")
public class PostReadController {

    private final PostReadService postReadService;

    // 모임 검색
    @GetMapping("/search")
    public ResponseEntity<Slice<PostsResponse>> searchPosts(@RequestParam(value = "keyword", defaultValue = "") String keyword,
                                                            @RequestParam(value = "address", defaultValue = "") String address,
                                                            @PageableDefault(sort = "id", direction = Sort.Direction.DESC, size = 8) Pageable pageable,
                                                            @RequestParam(value = "lastId", defaultValue = "" + Long.MAX_VALUE) Long lastId) {
        Page<PostsResponse> posts = postReadService.searchPosts(keyword, address, pageable, lastId);
        return ResponseEntity.status(HttpStatus.OK).body(posts);
    }

    // 카테고리별 모임 더보기
    @GetMapping
    public ResponseEntity<Slice<PostsResponse>> getPosts(@RequestParam(value = "category", defaultValue = "") String category,
                                                         @PageableDefault(sort = "id", direction = Sort.Direction.DESC, size = 8) Pageable pageable,
                                                         @RequestParam(value = "lastId", defaultValue = "" + Long.MAX_VALUE) Long lastId) {
        Page<PostsResponse> posts = postReadService.showPosts(category, pageable, lastId);
        return ResponseEntity.status(HttpStatus.OK).body(posts);
    }

    // 메인 페이지 카테고리별 모임
    @GetMapping("/list")
    public ResponseEntity<PostListDto> getPostList(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        PostListDto postList;
        if (userDetails == null) {
            postList = postReadService.showPostList();
        } else {
            postList = postReadService.showPostListMember(userDetails);
        }
        return ResponseEntity.status(HttpStatus.OK).body(postList);
    }

    // 모임 상세페이지
    @GetMapping("/detail/{postId}")
    public ResponseEntity<PostDetailResponse> postDetail(@PathVariable Long postId) {
        PostDetailResponse postDetail = postReadService.getPostDetail(postId);
        return ResponseEntity.status(HttpStatus.OK).body(postDetail);
    }

    // 내가 작성한 모임 조회
    @GetMapping("/mine")
    public ResponseEntity<List<MyPostsResponse>> getMyReadPost(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.status(HttpStatus.OK).body(postReadService.getMyReadPost(userDetails));
    }

    // 내가 참석한 모임 조회
    @GetMapping("/join")
    public ResponseEntity<List<JoinedPostsResponse>> getMyJoinPost(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.status(HttpStatus.OK).body(postReadService.getMyJoinPost(userDetails));
    }

}
