package com.example.modiraa.post.repository;

import com.example.modiraa.post.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findAllByIdLessThanAndTitleContainingAndAddressContains(Long lastId,
                                                                       String title,
                                                                       String address,
                                                                       Pageable pageable);
    Page<Post> findAllByCategoryContains(String category, Pageable pageable);
}
