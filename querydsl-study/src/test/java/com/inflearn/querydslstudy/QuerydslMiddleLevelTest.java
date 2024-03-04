package com.inflearn.querydslstudy;

import com.inflearn.querydslstudy.dto.MemberDto;
import com.inflearn.querydslstudy.dto.QMemberDto;
import com.inflearn.querydslstudy.dto.UserDto;
import com.inflearn.querydslstudy.entity.Member;
import com.inflearn.querydslstudy.entity.QMember;
import com.inflearn.querydslstudy.entity.Team;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

import static com.inflearn.querydslstudy.entity.QMember.member;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * 프로젝션 (select 대상 필드들) 과 결과 반환
 * - 1개인 경우 : 일반적인 상황.
 * - 2개 이상인 경우 : 타입을 명확하게 지정할 수 없기 때문에 튜플 or DTO로 조회
 */
@SpringBootTest
@Transactional
class QuerydslMiddleLevelTest {
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

    @DisplayName("프로젝션 결과가 2개 이상인 경우 ")
    @Test
    void tupleProjection() {
        List<Tuple> result = jpaQueryFactory
                .select(member.username, member.age)
                .from(member)
                .fetch();

        result.forEach(r -> {
                    assertThat(r.get(member.username)).isNotNull();
                    assertThat(r.get(member.age)).isNotNull();
        });
    }

    /**
     * Repository 계층에서 사용하는 Tuple 객체를 비즈니스 로직이나 바깥 계층으로 던질 때는 DTO로 변환하는 것을 권장함.
     */
    @DisplayName("1.new operation을 이용해 DTO 반환할 수 있다.(JPQL)")
    @Test
    void findDtoByJPQL() {
        List<MemberDto> result = em.createQuery("select new com.inflearn.querydslstudy.dto.MemberDto(m.username, m.age) from Member m", MemberDto.class)
                .getResultList();

        assertThat(result).extracting("username").containsAll(List.of("member1", "member2", "member3", "member4"));
        assertThat(result).extracting("age").containsAll(List.of(10, 20, 30, 40));
        assertThat(result).hasSize(4);
    }
    /**
     * QueryDsl 빈 생성 - 프로퍼티 접근,
     * Querydsl 이 조회된 필드들로 MemberDto를 만드는 것임.
     * - 꼭 기본 생성자가 필요함.
     * - Setter 이용해 세팅하므로 Setter 필요
     */
    @DisplayName("2.프로퍼티 접근(Setter) 통해 DTO 반환할 수 있다.(Querydsl)")
    @Test
    void findDtoBySetter() {
        List<MemberDto> result = jpaQueryFactory
                .select(Projections.bean(MemberDto.class,
                        member.username,
                        member.age))
                .from(member)
                .fetch();

        assertThat(result).extracting("username").containsAll(List.of("member1", "member2", "member3", "member4"));
        assertThat(result).extracting("age").containsAll(List.of(10, 20, 30, 40));
        assertThat(result).hasSize(4);
    }

    @DisplayName("3.Field 주입 통해 DTO 반환할 수 있다.(Querydsl)")
    @Test
    void findDtoByFieldInjection() {
        List<MemberDto> dtoResult = jpaQueryFactory
                .select(Projections.fields(MemberDto.class,
                        member.username,
                        member.age
                ))
                .from(member)
                .fetch();
        assertThat(dtoResult).extracting("username")
                .isNotNull();
        assertThat(dtoResult).extracting("age")
                .isNotNull();

        dtoResult.forEach(dto -> {
            assertThat(dto).isInstanceOf(MemberDto.class);
            System.out.println(dto);
        });
    }

    /**
     * 필드 주입 방식 - 조회하는 칼럼명과 반환해야하는 DTO의 필드명이 다를 때.
     * 별칭이 다름.
     */
    @DisplayName("3-1.Field 주입 시 DTO 필드 명이 다른 경우, 별칭을 사용할 수 있다.(simple)")
    @Test
    void findDtoByFieldInjection_useAlias_simple() {
        List<UserDto> dtoResult = jpaQueryFactory
                .select(Projections.fields(UserDto.class,
                        member.username.as("name"),
                        member.age
                ))
                .from(member)
                .fetch();

        assertThat(dtoResult).extracting("name")
                .isNotNull();
        assertThat(dtoResult).extracting("age")
                .isNotNull();

        dtoResult.forEach(dto -> {
            assertThat(dto).isInstanceOf(UserDto.class);
            System.out.println(dto);
        });
    }

    /**
     * 필드 주입 방식 - 서브 쿼리로, 조회하는 칼럼명과 반환해야하는 DTO의 필드명이 다를 때.
     * 별칭이 다름.
     * ExpressionUtils 사용
     */
    @DisplayName("3-2.Field 주입 시 DTO 필드 명이 다른 경우, ExpressionUtils 사용할 수 있다.(서브쿼리에 사용)")
    @Test
    void findDtoByFieldInjection_useAlias_subQuery() {
        QMember memberSub = new QMember("memberSub");
        List<UserDto> dtoResult = jpaQueryFactory
                .select(Projections.fields(UserDto.class,
                        member.username.as("name"),
                        ExpressionUtils.as(
                                JPAExpressions.select(memberSub.age.max()).from(memberSub)
                                , "age")
                ))
                .from(member)
                .where(member.age.eq(40))
//                .where(member.age.eq(memberSub.age))      // 외부 쿼리에서는 서브 쿼리 memberSub.age 인식 못함.
                .fetch();

        assertThat(dtoResult).extracting("name")
                .isNotNull();
        assertThat(dtoResult).extracting("age")     // max로 조회되는 age
                .isNotNull();

        dtoResult.forEach(dto -> {
            assertThat(dto).isInstanceOf(UserDto.class);
            assertThat(dto.getAge()).isEqualTo(40);
            assertThat(dtoResult).hasSize(1);
            System.out.println(dto);
        });
    }

    @DisplayName("4.생성자 통해 DTO 반환할 수 있다.(Querydsl)")
    @Test
    void findDtoByConstructor() {
        List<UserDto> dtoResult = jpaQueryFactory
                .select(Projections.constructor(UserDto.class,
                        member.username.as("name"),
                        member.age
                ))
                .from(member)
                .fetch();

        assertThat(dtoResult).extracting("name")
                .isNotNull();
        assertThat(dtoResult).extracting("age")
                .isNotNull();

        dtoResult.forEach(dto -> {
            assertThat(dto).isInstanceOf(UserDto.class);
            System.out.println(dto);
        });
    }

    /**
     * 컴파일로 오류 잡아줌.
     */
    @DisplayName("@QueryProjection 이용해 DTO 반환할 수 있다.(Querydsl)")
    @Test
    void findDtoByQueryProjection() {
        // Projections.constructor()은 잘못된 필드 입력해도 런타임 시 오류 발생..
        List<MemberDto> dtoResult = jpaQueryFactory
                .select(new QMemberDto(member.username, member.age))
                .from(member)
                .fetch();

        assertThat(dtoResult).extracting("username")
                .isNotNull();
        assertThat(dtoResult).extracting("age")
                .isNotNull();

        dtoResult.forEach(dto -> {
            assertThat(dto).isInstanceOf(MemberDto.class);
            System.out.println(dto);
        });
    }

    @DisplayName("BooleanBuilder를 이용해 동적 쿼리할 수 있다.(Querydsl)")
    @Test
    void dynamicQuery_BooleanBuilder() {
        String member1Name = "member1";
        int member1Age = 10;
        List<Member> member1Result = searchMember1(member1Name, member1Age);

        assertThat(member1Result).hasSize(1);

        assertThat(member1Result).extracting("username")
                .containsAll(List.of("member1"));
        assertThat(member1Result).extracting("age")
                .containsAll(List.of(10));
    }

    private List<Member> searchMember1(String nameCond, Integer ageCond) {
        BooleanBuilder whereBuilder = new BooleanBuilder();

        if (!Objects.isNull(nameCond)) {
            whereBuilder.and(member.username.eq(nameCond));
        }

        if (!Objects.isNull(ageCond)) {
            whereBuilder.and(member.age.eq(ageCond));
        }

        return jpaQueryFactory
                .selectFrom(member)
                .where(whereBuilder)
                .fetch();
    }

    @DisplayName("where절을 콤마로 연결하면 필드가 null인 경우 해당 조건을 무시할 수 있다.")
    @Test
    void dynamicQuery_whereParam() {
        String member2Name = "member2";
        int member2Age = 20;
        List<Member> member2Result = searchMember2(member2Name, member2Age);
        assertThat(member2Result).hasSize(1);

        assertThat(member2Result).extracting("username")
                .containsAll(List.of("member2"));
        assertThat(member2Result).extracting("age")
                .containsAll(List.of(20));
    }

    /**
     * 장점 1. where 조건 내에 Null값 필드는 무시된다.
     * 장점 2. 조건 메서드를 다른 쿼리에서도 재사용할 수 있다.
     *    ㄴ 조건 메서드들을 조합하여 의미를 전달할 수 있다.
     *    Ex) isServiceable 메서드 : '노출 가능' && '기간 내에 존재' 의 조건메서드 조합
     * 장점 3. 쿼리 자체의 가독성이 높아진다.
     *
     */
    private List<Member> searchMember2(String usernameCond, int ageCond) {
        return jpaQueryFactory
                .selectFrom(member)
                .where(usernameEq(usernameCond), ageEq(ageCond))
                .fetch();
    }

    private BooleanExpression ageEq(Integer ageCond) {
        return ageCond != null ? member.age.eq(ageCond) : null;
    }

    private BooleanExpression usernameEq(String usernameCond) {
        return usernameCond != null ? member.username.eq(usernameCond) :  null;
    }

    /**
     * 위 메서드들을 이용해서 where().and() 에 사용/조립할 수도 있다.
     * - 아래의 경우 null 처리는 따로 해줘야 한다.
     */
    private BooleanExpression allEq(String usernameCond, Integer ageCond) {
        return usernameEq(usernameCond).and(ageEq(ageCond));
    }
}
