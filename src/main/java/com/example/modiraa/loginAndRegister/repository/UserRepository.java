package com.example.modiraa.loginAndRegister.repository;

import com.example.modiraa.loginAndRegister.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Member, Long>{
    Optional<Member> findByUsername(String username);
    Optional<Member> findByNickname(String nickname);
}