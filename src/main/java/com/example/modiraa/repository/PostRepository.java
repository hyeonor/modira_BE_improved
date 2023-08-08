package com.example.modiraa.repository;

import com.example.modiraa.dto.response.MyPostsResponse;
import com.example.modiraa.model.Member;
import com.example.modiraa.model.Post;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    Post findByChatRoomId(Long chatRoomId);

    // 내가 작성한 모임 조회
    @Query("SELECT new com.example.modiraa.dto.response.MyPostsResponse(p.id, p.title, PI.imageUrl, p.menu)" +
            "from Post p left outer join PostImage PI on PI.menu=p.menu " +
            "where p .member =:member " +
            "order by p.id desc")
    List<MyPostsResponse> MyPostRead(@Param("member") Member member, Pageable pageable);
}
