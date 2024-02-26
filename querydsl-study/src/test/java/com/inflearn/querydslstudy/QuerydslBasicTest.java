package com.inflearn.querydslstudy;

import com.inflearn.querydslstudy.entity.Member;
import com.inflearn.querydslstudy.entity.QMember;
import com.inflearn.querydslstudy.entity.Team;
import com.querydsl.core.QueryResults;
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
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class QuerydslBasicTest {
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

        em.flush(); // 쿼리 생성
        em.clear(); // 캐시 날림
    }

    @Test
    @DisplayName("Querydsl - where 조건절")
    void querydsl_findByUsername() {
        QMember member = QMember.member;        // 기본 인스턴스 사용
//        QMember member = new QMember("m");    // 하나의 테이블로 조인할 경우, 다른 별칭을 지정하여 Q타입 생성할 수 있음.

        Member result = jpaQueryFactory.selectFrom(member)
                .where(member.username.eq("member1"))
                .fetchOne();
        assertThat(result.getUsername()).isEqualTo("member1");

    }

    @Test
    @DisplayName("Querydsl - where 조건문 관련 기능들 (eq, gt, goe, in, like, contains)")
    void searchWithWhere() {
        Member member2 = jpaQueryFactory.selectFrom(member)
                .where(member.username.ne("member1")
                        .and(member.age.eq(20)))
                .fetchOne();

        Member member3 = jpaQueryFactory.selectFrom(member)
                .where(member.username.eq("member3")
                        .and(member.age.gt(20)))
                .fetchOne();

        Member member4 = jpaQueryFactory.selectFrom(member)
                .where(member.username.eq("member4")
                        .and(member.age.goe(40)))
                .fetchOne();


        List<Member> members = jpaQueryFactory.selectFrom(member)
                .where(member.username.like("%member%")
                        .and(member.age.goe(10)))
                .fetch();

        // and 조건을 콤마로 연결할 수 있음. AND 조건 중 null인 값은 무시됨.
        List<Member> membersTenToThirty = jpaQueryFactory.selectFrom(member)
                .where(member.username.contains("member"), member.age.in(10,20,30))
                .fetch();

        assertThat(member2.getUsername()).isEqualTo("member2");
        assertThat(member3.getUsername()).isEqualTo("member3");
        assertThat(member4.getUsername()).isEqualTo("member4");
        assertThat(members).hasSize(4);
        assertThat(membersTenToThirty).hasSize(3);

    }
    @Test
    @DisplayName("Querydsl - 정렬")
    void sorting() {
        em.persist(new Member(null, 100));
        // 이름에 "member"를 포함하며, 나이로 내림차순 후 이름으로 내림차순한다.
        // nullsLast() : null인 경우 마지막에 조회된다.
        List<Member> members = jpaQueryFactory.selectFrom(member)
                .where(member.age.loe(100))
                .orderBy(member.age.desc(), member.username.desc().nullsLast())
                .fetch();

        assertThat(members.get(0).getAge()).isEqualTo(100);
        assertThat(members.get(0).getUsername()).isNull();
        assertThat(members.get(1).getAge()).isEqualTo(40);
        assertThat(members.get(2).getAge()).isEqualTo(30);
        assertThat(members.get(3).getAge()).isEqualTo(20);
        assertThat(members.get(4).getAge()).isEqualTo(10);


        List<Member> nullFirstMembers = jpaQueryFactory.selectFrom(member)
                .where(member.age.goe(30))
                .orderBy(member.username.desc().nullsFirst())
                .fetch();
        assertThat(nullFirstMembers.get(0).getAge()).isEqualTo(100);
        assertThat(nullFirstMembers).hasSize(3);
    }

    @Test
    @DisplayName("Querydsl - 페이징 (offset: 0부터 가능)")
    void paging() {
        QueryResults<Member> results = jpaQueryFactory.selectFrom(member)
                .orderBy(member.username.desc().nullsFirst())       // null이 먼저 조회된다.
                .offset(0)
                .limit(5)
                .fetchResults();// total count 쿼리가 추가 실행됨.
        assertThat(results.getResults()).hasSize(4);   // 전체 조회 수
        assertThat(results.getLimit()).isEqualTo(5);    // 최대 5건 조회
        assertThat(results.getOffset()).isZero();
    }

    @Test
    @DisplayName("Querydsl - 집합 함수(count, sum, max, min, avg)")
    void aggregation() {
        Tuple tuple = jpaQueryFactory
                .select(
                        member.count(),
                        member.age.sum(),
                        member.age.max(),
                        member.age.min())
                .from(member)
                .fetchOne();

        assertThat(tuple.get(member.count())).isEqualTo(4);
        assertThat(tuple.get(member.age.sum())).isEqualTo(100);
        assertThat(tuple.get(member.age.max())).isEqualTo(40);
        assertThat(tuple.get(member.age.min())).isEqualTo(10);
    }

    @Test
    @DisplayName("Querydsl - Group by : 팀의 이름과 각 팀의 평균 연령을 구한다.")
    void groupBy() {
        List<Tuple> results = jpaQueryFactory
                .select(
                        team.name,
                        member.age.avg()
                )
                .from(member)
                .join(member.team, team)    // join(조인 대상, 별칭으로 사용할 Q타입)
                .groupBy(team.name)
                .fetch();

        Tuple teamA = results.get(0);
        Tuple teamB = results.get(1);

        assertThat(teamA.get(team.name)).isEqualTo("teamA");
        assertThat(teamB.get(team.name)).isEqualTo("teamB");
        assertThat(teamA.get(member.age.avg())).isEqualTo(15);
        assertThat(teamB.get(member.age.avg())).isEqualTo(35);
    }

}
