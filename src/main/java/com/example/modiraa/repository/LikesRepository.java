package com.example.modiraa.repository;

import com.example.modiraa.model.Like;
import com.example.modiraa.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LikesRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByGiverAndReceiver(Member giver, Member receiver);

    // 라잌스에 리시버가 변수안에 있는 리시버일때만 좋아요를 받아 카운트 센다.
    @Query("select count(l) from Like l where l.receiver = :receiver ")
    Long likesCount(@Param("receiver") Member receiver);
}