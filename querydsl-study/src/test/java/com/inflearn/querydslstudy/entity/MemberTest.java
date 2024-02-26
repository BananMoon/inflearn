package com.inflearn.querydslstudy.entity;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.inflearn.querydslstudy.entity.QMember.member;

@SpringBootTest
@Transactional
//@Commit     // rollback 진행 X
class MemberTest {

    @Autowired
    EntityManager em;

    @Test
    void jpqlTest() {
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);

        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        em.flush(); // 쿼리 생성
        em.clear(); // 캐시 날림

        List<Member> members = em.createQuery("select m from Member m", Member.class)
                .getResultList();

        for (Member m : members) {
            System.out.println("member = " + member);
            System.out.println("team = " + member.team);
        }
    }
}