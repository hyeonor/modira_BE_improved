package com.example.modiraa.repository;

import com.example.modiraa.model.Dislike;
import com.example.modiraa.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface DislikeRepository extends JpaRepository<Dislike, Long> {
    Optional<Dislike> findByGiverAndReceiver(Member giver, Member receiver);

    // Dislike에 리시버가 변수안에 있는 리시버일때만 싫어요를  받아 카운트 센다.
    @Query("select count(h) from Dislike h where h.receiver = :receiver ")
    Long hatesCount(@Param("receiver") Member receiver);
}
