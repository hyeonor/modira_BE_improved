package com.example.modiraa.LikeAndHate.repository;

import com.example.modiraa.LikeAndHate.model.Likes;
import com.example.modiraa.loginAndRegister.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LikesRepository extends JpaRepository<Likes,Long> {
    Optional<Likes> findByGiverAndReceiver(Member giver, Member receiver);

    // 라잌스에 리시버가 변수안에 있는 리시버일때만 좋아요를  받아 카운트 센다.
    @Query("select count(l) from Likes l where l.receiver = :receiver ")
    Long likesCount(@Param("receiver")Member receiver);

}