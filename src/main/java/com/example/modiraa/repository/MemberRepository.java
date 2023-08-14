package com.example.modiraa.repository;

import com.example.modiraa.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {
    Optional<Member> findByNickname(String nickname);

    <T> Optional<T> findByNickname(String nickname, Class<T> type);
}