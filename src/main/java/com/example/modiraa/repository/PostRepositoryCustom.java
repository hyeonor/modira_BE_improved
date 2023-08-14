package com.example.modiraa.repository;

import com.example.modiraa.dto.response.MyPostsResponse;
import com.example.modiraa.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostRepositoryCustom {
    Page<Post> findBySearchKeywordAndAddress(Long lastId, String address, String keyword, Pageable pageable);

    Page<Post> findByIdLessThanAndCategory(Long lastId, String category, Pageable pageable);

    Page<Post> findAllPosts(Pageable pageable);

    Page<Post> findByCategory(String category, Pageable pageable);

    Page<Post> findAllByAddress(String address, Pageable pageable);

    Page<Post> findByAddressAndCategory(String address, String category, Pageable pageable);

    List<MyPostsResponse> findMyPostsByMemberOrderByDesc(Long memberId);
}
