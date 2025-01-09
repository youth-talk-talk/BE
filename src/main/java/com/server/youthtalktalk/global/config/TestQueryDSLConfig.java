package com.server.youthtalktalk.global.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.server.youthtalktalk.domain.post.repostiory.PostRepositoryCustom;
import com.server.youthtalktalk.domain.post.repostiory.PostRepositoryCustomImpl;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.context.Te

@TestConfiguration
public class TestQueryDSLConfig {
    @PersistenceContext
    private EntityManager entityManager;

    @Bean
    public JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(entityManager);
    }

    @Bean
    public PostRepositoryCustom postRepositoryCustom(){
        return new PostRepositoryCustomImpl(jpaQueryFactory());
    }
}
