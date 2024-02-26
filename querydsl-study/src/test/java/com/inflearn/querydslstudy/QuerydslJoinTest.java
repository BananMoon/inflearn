package com.inflearn.querydslstudy;

import com.inflearn.querydslstudy.entity.Member;
import com.inflearn.querydslstudy.entity.Team;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.inflearn.querydslstudy.entity.QMember.member;
import static com.inflearn.querydslstudy.entity.QTeam.team;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class QuerydslJoinTest {

    @PersistenceContext
    EntityManager em;

    JPAQueryFactory jpaQueryFactory;

    @BeforeEach
    void setup() {
        jpaQueryFactory = new JPAQueryFactory(em);

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

        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("Querydsl - Group by & Having : 결과를 제한한다")
    void groupByHaving() {
        List<Tuple> resultsGoeAge20 = jpaQueryFactory
                .select(
                        team.name,
                        member.age.avg()
                )
                .from(member)
                .join(member.team, team)
                .groupBy(team.name)
                .having(member.age.avg().goe(20))
                .fetch();

        assertThat(resultsGoeAge20).hasSize(1);

        Tuple teamB = resultsGoeAge20.get(0);
        assertThat(teamB.get(member.age.avg())).isEqualTo(35);
    }

    @Test
    @DisplayName("Querydsl - 세타조인: 연관관계가 없는 테이블 간 조인")
    void settajoin() {
        Team member1Team = new Team("member1");
        em.persist(member1Team);
        em.flush();
        em.clear();

        List<Member> fetch = jpaQueryFactory
                .select(member)
                .from(member, team)    // 연관관계 없는 member와 team
                .where(member.username.eq(team.name))
                .fetch();

        assertThat(fetch).hasSize(1);
        assertThat(fetch)
                .extracting("username")     // 조회한 member의 필드 추출
                .containsExactly("member1");
    }

    /**
     * 요구 사항
     * - 회원과 팀에 대해 조회하는데, 회원은 무조건 조회되어야 한다.
     * - 팀은 이름이 'teamA'인 팀만 조회되어야 한다.
     */
    @Test
    @DisplayName("Querydsl - 외부 조인: 연관관계 있는 테이블 간 외부 조인")
    void outerjoin() {
        List<Tuple> tuples = jpaQueryFactory
                .select(member, team)
                .from(member)
                .leftJoin(member.team, team)
                .on(team.name.eq("teamA"))
                .fetch();

        assertThat(tuples).hasSize(4);
        // 조회된 데이터 확인
        for (Tuple t : tuples) {
            System.out.println(t);
            if (t instanceof Team) {
                assertThat(t.get(team).getName()).isEqualTo("teamA");
            }
        }
    }
    @Test
    @DisplayName("연관관계가 없는 테이블 간 외부 조인 가능. (on절 이용)")
    void settaouterjoin() {

    }
}
