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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class PostReadController {

    private final PostReadService postReadService;

    // 모임 검색
    @GetMapping("/api/search/post")
    public ResponseEntity<Slice<PostsResponseDto>> searchPosts(@RequestParam(value = "keyword", defaultValue = "") String keyword,
                                                               @RequestParam(value = "address", defaultValue = "") String address,
                                                               @PageableDefault(sort = "id", direction = Sort.Direction.DESC, size = 8) Pageable pageable,
                                                               @RequestParam(value = "lastId", defaultValue = "" + Long.MAX_VALUE) Long lastId) {
        Page<PostsResponseDto> posts = postReadService.searchPosts(keyword, address, pageable, lastId);
        return ResponseEntity.status(HttpStatus.OK).body(posts);
    }

    // 카테고리별 모임 더보기
    @GetMapping("/api/post")
    public ResponseEntity<Slice<PostsResponseDto>> getPosts(@RequestParam(value = "category", defaultValue = "") String category,
                                                            @PageableDefault(sort = "id", direction = Sort.Direction.DESC, size = 8) Pageable pageable,
                                                            @RequestParam(value = "lastId", defaultValue = "" + Long.MAX_VALUE) Long lastId) {
        Page<PostsResponseDto> posts = postReadService.showPosts(category, pageable, lastId);
        return ResponseEntity.status(HttpStatus.OK).body(posts);
    }

    // 메인 페이지 카테고리별 모임
    @GetMapping("/api/post/list")
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
    @GetMapping("/api/post/detail/{postId}")
    public ResponseEntity<PostDetailResponseDto> postDetail(@PathVariable Long postId) {
        PostDetailResponseDto postDetail = postReadService.getPostDetail(postId);
        return ResponseEntity.status(HttpStatus.OK).body(postDetail);
    }

    // 내가 작성한 모임 조회
    @GetMapping("/api/myposts")
    public ResponseEntity<List<MyPostsResponseDto>> getMyReadPost(@AuthenticationPrincipal UserDetailsImpl userDetails) throws IllegalAccessException {

        return ResponseEntity.status(HttpStatus.OK).body(postReadService.getMyReadPost(userDetails));
    }

    // 내가 참석한 모임 조회
    @GetMapping("/api/myjoin")
    public ResponseEntity<List<EnterPostsResponseDto>> getMyJoinPost(@AuthenticationPrincipal UserDetailsImpl userDetails) throws IllegalAccessException {

        return ResponseEntity.status(HttpStatus.OK).body(postReadService.getMyJoinPost(userDetails));
    }

}
