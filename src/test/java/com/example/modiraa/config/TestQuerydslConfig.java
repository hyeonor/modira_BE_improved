package com.example.modiraa.config;

import com.example.modiraa.repository.ChatMessageQueryRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import javax.persistence.EntityManager;

@TestConfiguration
public class TestQuerydslConfig {
    @Autowired
    private EntityManager entityManager;

    @Bean
    public JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(entityManager);
    }

    @Bean
    public ChatMessageQueryRepository chatMessageQueryRepository() {
        return new ChatMessageQueryRepository(jpaQueryFactory());
    }
}
