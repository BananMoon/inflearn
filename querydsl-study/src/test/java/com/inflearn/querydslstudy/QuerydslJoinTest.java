package com.inflearn.querydslstudy;

import com.inflearn.querydslstudy.entity.Member;
import com.inflearn.querydslstudy.entity.QMember;
import com.inflearn.querydslstudy.entity.Team;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.inflearn.querydslstudy.entity.QMember.member;
import static com.inflearn.querydslstudy.entity.QTeam.team;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * 1. groupby & having
 * 2. 연관관계 없는 엔티티간 내부조인 / 외부조인  (+연관관계 있는 엔티티간 외부조인)
 * 3. fetch join
 * 4. 서브 쿼리
 * 5. Case문
 * 6. 상수 조회 / 문자더하기
 */
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
    @DisplayName("Querydsl - 세타조인: 연관관계가 없는 테이블 간 내부 조인")
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

    /**
     * 연관관계 없는 엔티티 외부 조인 -> A * B 곱셈 조인
     * 회원의 이름이 팀 이름과 같은 대상 외부 조인
     * 연관관계 있는 외부 조인의 경우, `from(member).leftjoin(member.team, team).on(member.user.name.eq(team.name)).fetch()`로
     *  작성하고 생성되는 쿼리문에는 id 값 비교하는 on 절이 들어가게 됨.
     * 연관관계 없는 외부 조인 : `from(member).leftjoin(team).on(member.username.eq(team.name))`로 작성하고
     *  생성되는 쿼리문에는 id 값 비교 on 절 없음.
     *
     */
    @Test
    @DisplayName("연관관계가 없는 테이블 간 외부 조인 가능. (on절 이용)")
    void join_no_relation() {
        em.persist(new Member("teamA"));
        em.persist(new Member("teamB"));
        em.persist(new Member("teamC"));

        List<Tuple> result = jpaQueryFactory
                .select(member, team)
                .from(member)
                .leftJoin(team).on(member.username.eq(team.name))
//                .leftJoin(member.team, team).on(member.username.eq(team.name))
                .fetch();

        // 조회된 데이터 확인 : leftjoin이므로 member는 전체 조회, team은 member와 이름이 같은 데이터만 조회됨.
        for (Tuple t : result) {
            System.out.println(t);
            if (t instanceof Team) {
                assertThat(t.get(team).getName()).isEqualTo(t.get(member).getUsername());
            }
        }
    }

    @PersistenceUnit
    EntityManagerFactory emp;
    /**
     * SQL에서 제공하는 기능 X. JPA에서 제공하는 기능.
     * SQL 조인을 활용해서 연관된 엔티티를 한개의 쿼리로 조회하는 기능.
     * 성능 최적화에 사용하는 방법
     */
    @DisplayName("LAZY 세팅인 연관 엔티티는 함께 조회되지 않는다.")
    @Test
    void fetch_join_no() {
        em.flush();
        em.clear();
        // LAZY 세팅이기 때문에 Team 이 조회되지 않음.
        Member findMember = jpaQueryFactory.selectFrom(member)
                .where(member.username.eq("member1"))
                .fetchOne();

        // 확인 방법 : EntityManagerFactory 통해 해당 Entity(Team)가 초기화된 상태인지 확인할 수 있음.
        boolean isTeamLoaded = emp.getPersistenceUnitUtil().isLoaded(findMember.getTeam());

        assertThat(isTeamLoaded).isFalse();
    }
    @DisplayName("fetch join을 사용하여 LAZY 세팅인 연관 엔티티여도 함께 조회되도록 한다.")
    @Test
    void fetch_join() {
        em.flush();
        em.clear();
        // LAZY 세팅이기 때문에 Team 이 조회되지 않음.
        Member findMember = jpaQueryFactory.selectFrom(member)
                .join(member.team, team).fetchJoin()
                .where(member.username.eq("member1"))
                .fetchOne();

        // 확인 방법 : EntityManagerFactory 통해 해당 Entity(Team)가 초기화된 상태인지 확인할 수 있음.
        boolean isTeamLoaded = emp.getPersistenceUnitUtil().isLoaded(findMember.getTeam());

        assertThat(isTeamLoaded).isTrue();
    }

    /**
     * 나이가 가장 많은 회원 조회
     */
    @DisplayName("JPAExpressions를 이용해 where 절에 서브쿼리를 사용할 수 있다.")
    @Test
    void subQuery() {
        em.persist(new Member("older", 40));

        // 내부와 외부 쿼리의 alias가 달라야 하므로 QMember 생성
        QMember memberSub = new QMember("memberSub");
        List<Member> findMembers = jpaQueryFactory.selectFrom(member)
                .where(member.age.eq(
                        JPAExpressions.select(memberSub.age.max())
                                .from(memberSub)
                ))
                .fetch();

        assertThat(findMembers).hasSize(2);
        assertThat(findMembers).extracting("age")
//                .containsExactly(40)   // 데이터가 2개 이상 조회될 경우 사용하지 못함.
                .containsAll(List.of(40));  // 해당 값이 조회 데이터에 포함되면 됨.
//                .containsAnyElementsOf(List.of(40, 30));   // 조회된 데이터에 하나라도 포함되면 됨. 잘 안쓸 듯.
    }


    /**
     * Case문 :
     * 1. 간단한 경우, when().then()
     * 2. 복잡한 경우, CaseBuilder 이용
     */
    @DisplayName("when().then() 이용하여 간단 Case문 작성할 수 있다.")
    @Test
    void basicCase() {
        List<String> result = jpaQueryFactory
                .select(member.age
                        .when(10).then("10살")
                        .when(20).then("스무살")
                        .otherwise("기타")
                )
                .from(member)
                .fetch();
        result.forEach(System.out::println);
        assertThat(result).containsAll(List.of("10살", "스무살", "기타"));
    }

    /**
     * Case문
     * - CaseBuilder 이용
     * - between(A,B) : A 부터 B 포함(이하)까지
     */
    @DisplayName("CaseBuilder 이용하여 복잡한 Case문을 작성할 수 있다.")
    @Test
    void complexCase() {
        List<String> resultByCase = jpaQueryFactory
                .select(new CaseBuilder()
                        .when(member.age.between(10, 19)).then("10대")
                        .when(member.age.between(20, 29)).then("20대")
                        .when(member.age.between(30, 39)).then("30대")
                        .otherwise("기타")
                ).from(member)
                .fetch();

        resultByCase.forEach(System.out::println);
            assertThat(resultByCase).containsAll(List.of("10대", "20대", "30대", "기타"));

    }

    @DisplayName("상수를 조회해야할 때 Expressions.constant()를 사용할 수 있다.")
    @Test
    void constant() {
        List<Tuple> result = jpaQueryFactory
                .select(member.username, Expressions.constant("A"))
                .from(member)
                .fetch();

        result.forEach(r-> System.out.println(r.get(Expressions.constant("A"))));

        result.forEach(r -> assertThat(r.get(1, String.class)).contains("A"));
    }
    /**
     * concat() : 같은 타입일 때만 사용 가능하므로 타입을 통일해야 함.
     */
    @DisplayName("{멤버 이름}_{멤버나이} 형태로 문자 더하기 하고 싶을 때 ")
    @Test
    void concat() {
        List<String> result = jpaQueryFactory
                .select(member.username.concat("_").concat(member.age.stringValue()))
                .from(member)
                .where(member.username.eq("member1"))
                .fetch();

        result.forEach(r -> assertThat(r).contains("member1_"));
    }
}
