package com.example.modiraa.repository;

import com.example.modiraa.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByNickname(String nickname);

    <T> Optional<T> findByNickname(String nickname, Class<T> type);

    Member findAllById(Long Id);

    @Query("SELECT m FROM Member m WHERE m.oAuthId =:oAuthId")
    Optional<Member> findByOAuthId(String oAuthId);
}