package com.example.modiraa.repository;

import com.example.modiraa.model.Like;
import com.example.modiraa.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByGiverAndReceiver(Member giver, Member receiver);
}