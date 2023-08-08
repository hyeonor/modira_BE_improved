package com.example.modiraa.repository;

import com.example.modiraa.model.Dislike;
import com.example.modiraa.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DislikeRepository extends JpaRepository<Dislike, Long> {
    Optional<Dislike> findByGiverAndReceiver(Member giver, Member receiver);
}
