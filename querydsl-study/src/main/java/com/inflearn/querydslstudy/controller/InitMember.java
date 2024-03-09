package com.inflearn.querydslstudy.controller;

import com.inflearn.querydslstudy.entity.Member;
import com.inflearn.querydslstudy.entity.Team;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * local 환경에서 RestAPI 테스트하기 위해 먼저 Member 100명 insert 수행되도록 하는 컴포넌트
 */
@Profile("local")
@Component
@RequiredArgsConstructor
public class InitMember {
    private final InitMmeberService initMmeberService;
    @PostConstruct
    void setUp() {
        initMmeberService.init100();
    }

    @Component
    @RequiredArgsConstructor
    static class InitMmeberService {
        @PersistenceContext     // Persistence Context 사용
        EntityManager em;

        @Transactional
        public void init100() {
            Team teamA = new Team("teamA");
            Team teamB = new Team("teamB");
            em.persist(teamA);
            em.persist(teamB);

            for (int i = 0; i < 100; i++) {
                Team selectedTeam = i % 2 == 0 ? teamA : teamB;
                em.persist(new Member("member" + i, i, selectedTeam));
            }
        }
    }
}
