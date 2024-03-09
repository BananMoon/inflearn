package com.inflearn.querydslstudy;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class QuerydslStudyApplication {

    public static void main(String[] args) {
        SpringApplication.run(QuerydslStudyApplication.class, args);
    }

/*
     JpaQueryFactory에 대한 동시성 문제는 EntityManager에 의존함. EM은 Spring과 엮여 사용하면 트랜잭션 단위로 분리되어 동작함.
     Spring에서 EntityManager에 Proxy 객체를 주입하여 트랜잭션 단위로 바운딩되도록 라우팅해줌.
    @Bean
    public JPAQueryFactory jpaQueryFactory(EntityManager em) {
        return new JPAQueryFactory(em);
    }
*/
}
