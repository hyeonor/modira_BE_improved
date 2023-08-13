package com.example.modiraa.repository;

import com.example.modiraa.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    Optional<Post> findByChatRoomId(Long chatRoomId);
}
