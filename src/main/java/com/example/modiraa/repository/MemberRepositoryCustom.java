package com.example.modiraa.repository;

import com.example.modiraa.model.Member;

import java.util.Optional;

public interface MemberRepositoryCustom {
    Optional<Member> findByOAuthId(String oAuthId);
}
