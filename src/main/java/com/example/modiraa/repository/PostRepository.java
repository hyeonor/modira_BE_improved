package com.example.modiraa.repository;

import com.example.modiraa.dto.response.MyPostsResponseDto;
import com.example.modiraa.model.Member;
import com.example.modiraa.model.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    Post findByChatRoomId(Long chatRoomId);

    // 룸아이디 꺼내기
    Post findByTitle(String title);

    // 내가 작성한 모임 조회
    @Query("SELECT new com.example.modiraa.dto.response.MyPostsResponseDto(p.id, p.title, PI.imageurl, p.menu)" +
            "from Post p left outer join PostImage PI on PI.menu=p.menu " +
            "where p .member =:member " +
            "order by p.id desc")
    List<MyPostsResponseDto> MyPostRead(@Param("member") Member member, Pageable pageable);
}
