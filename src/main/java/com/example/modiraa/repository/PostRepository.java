package com.example.modiraa.repository;

import com.example.modiraa.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
    Post findByChatRoomId(Long chatRoomId);
}
