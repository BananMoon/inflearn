package com.inflearn.querydslstudy.repository;

import com.inflearn.querydslstudy.dto.MemberSearchCondition;
import com.inflearn.querydslstudy.dto.MemberTeamDto;
import com.inflearn.querydslstudy.dto.QMemberTeamDto;
import com.inflearn.querydslstudy.entity.Member;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

import static com.inflearn.querydslstudy.entity.QMember.member;
import static com.inflearn.querydslstudy.entity.QTeam.team;

public class MemberRepositoryImpl implements MemberRepositoryCustom {
    private final JPAQueryFactory queryFactory;  // Bean으로 등록해도 됨.

    public MemberRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }
    @Override
    public List<MemberTeamDto> searchByWhere(MemberSearchCondition condition) {
        return queryFactory
                .select(new QMemberTeamDto(
                        member.id.as("memberId"),
                        member.username,
                        member.age,
                        team.id.as("teamId"),
                        team.name.as("teamName")
                ))
                .from(member)
                .leftJoin(member.team, team)
                .where(usernameEq(condition.getUsername()),
                        teamNameEq(condition.getTeamName()),
                        ageGoe(condition.getAgeGoe()),
                        ageLoe(condition.getAgeLoe()))
                .fetch();

    }

    /*
    - .fetchResults() 로 조회 시, 컨텐츠 쿼리, count 쿼리 2개 모두 수행된다.
    - 컨텐츠 / count 쿼리를 분리하면 count 쿼리에서는 정렬 관련 쿼리문은 포함시키지 않도록 하여 성능 최적화한다.
    - 컨텐츠 조회했는데 데이터가 없는 경우 count 쿼리는 실행하지 않도록 한다.
     */
    @Override
    public Page<MemberTeamDto> searchPageComplex(MemberSearchCondition condition, Pageable pageable) {
        List<MemberTeamDto> content = queryFactory
                .select(new QMemberTeamDto(
                        member.id.as("memberId"),
                        member.username,
                        member.age,
                        team.id.as("teamId"),
                        team.name.as("teamName")
                ))
                .from(member)
                .leftJoin(member.team, team)
                .where(usernameEq(condition.getUsername()),
                        teamNameEq(condition.getTeamName()),
                        ageGoe(condition.getAgeGoe()),
                        ageLoe(condition.getAgeLoe()))
                .offset(pageable.getOffset())       // 시작 페이지
                .limit(pageable.getPageSize())      // 1번 조회 시 조회 데이터 갯수
                .fetch();

        Long total = queryFactory
                .select(member.count()) // count(member_id) 로 수행됨.
                .from(member)
                .leftJoin(member.team, team)
                .where(usernameEq(condition.getUsername()),
                        teamNameEq(condition.getTeamName()),
                        ageGoe(condition.getAgeGoe()),
                        ageLoe(condition.getAgeLoe()))
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }

    /*
    Deprecated
    count 쿼리가 생략 가능한 경우 수행되지 않도록 스프링 데이터 JPA 에서 제공하는 기능.
    아래 경우에서 생략.
         1. 페이지 시작이면서 컨텐츠 크기가 페이지 사이즈보다 작을 때
         2. 마지막 페이지일 때
     */
    public Page<MemberTeamDto> searchPage_fetchCount(MemberSearchCondition condition, Pageable pageable) {
        List<MemberTeamDto> content = queryFactory
                .select(new QMemberTeamDto(
                        member.id.as("memberId"),
                        member.username,
                        member.age,
                        team.id.as("teamId"),
                        team.name.as("teamName")
                ))
                .from(member)
                .leftJoin(member.team, team)
                .where(usernameEq(condition.getUsername()),
                        teamNameEq(condition.getTeamName()),
                        ageGoe(condition.getAgeGoe()),
                        ageLoe(condition.getAgeLoe()))
                .offset(pageable.getOffset())       // 시작 페이지
                .limit(pageable.getPageSize())      // 1번 조회 시 조회 데이터 갯수
                .fetch();

        JPAQuery<Member> countQuery = queryFactory
                .select(member) // count(member_id) 로 수행됨.
                .from(member)
                .leftJoin(member.team, team)
                .where(usernameEq(condition.getUsername()),
                        teamNameEq(condition.getTeamName()),
                        ageGoe(condition.getAgeGoe()),
                        ageLoe(condition.getAgeLoe()));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchCount);
    }

    /**
     * Deprecated fetchCount 대신 사용할 수 있는 방식
     * @param condition
     * @param pageable
     * @return
     */
    public Page<MemberTeamDto> searchPage_count(MemberSearchCondition condition, Pageable pageable) {
        // contents
        List<MemberTeamDto> content = queryFactory
                .select(new QMemberTeamDto(
                        member.id.as("memberId"),
                        member.username,
                        member.age,
                        team.id.as("teamId"),
                        team.name.as("teamName")
                ))
                .from(member)
                .leftJoin(member.team, team)
                .where(usernameEq(condition.getUsername()),
                        teamNameEq(condition.getTeamName()),
                        ageGoe(condition.getAgeGoe()),
                        ageLoe(condition.getAgeLoe()))
                .offset(pageable.getOffset())       // 시작 페이지
                .limit(pageable.getPageSize())      // 1번 조회 시 조회 데이터 갯수
                .fetch();
        System.out.println("조회 시작 데이터 Number (pageable.offset) : " + pageable.getOffset());
        System.out.println("한 페이지당 데이터 갯수 (pageable.pageSize) : " + pageable.getPageSize());
        System.out.println("요청한 페이지 번호 (pageable.PageNumber) : " + pageable.getPageNumber());
        System.out.println("조회 전체 크기(content.size) : " + content.size());

        // 방법 1.
        // 시작 페이지
        // 마지막 페이지 : 데이터가 없거나, 데이터 크기가 pageSize보다 작은 경우
        // 한계점 1개 - 데이터크기가 딱 pageSize랑 같을 때는 카운트 쿼리를 피할 수 없는 듯함.
        if ((pageable.getOffset() == 0 && content.size() <= pageable.getPageSize())
            || (content.isEmpty() || content.size() < pageable.getPageSize())) {
            return new PageImpl<>(content, pageable, content.size());
        }

        Long count = queryFactory
                .select(member.count()) // count(member_id) 로 수행됨.
                .from(member)
                .leftJoin(member.team, team)
                .where(usernameEq(condition.getUsername()),
                        teamNameEq(condition.getTeamName()),
                        ageGoe(condition.getAgeGoe()),
                        ageLoe(condition.getAgeLoe()))
                .fetchOne();
        return new PageImpl<>(content, pageable, count);
        // 방법 2. PageableExecutionUtils 사용!
        // 한계점 2개 - 첫번째 페이지일 때는 count 쿼리 수행 안되는데, 마지막 페이지의 경우에는 수행됨..
        /*JPAQuery<Long> countQuery = queryFactory
                .select(member.count()) // count(member_id) 로 수행됨.
                .from(member)
                .leftJoin(member.team, team)
                .where(usernameEq(condition.getUsername()),
                        teamNameEq(condition.getTeamName()),
                        ageGoe(condition.getAgeGoe()),
                        ageLoe(condition.getAgeLoe()));
        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);*/
    }

    private BooleanExpression usernameEq(String username) {
        return StringUtils.hasText(username)? member.username.eq(username) : null;
    }
    private BooleanExpression teamNameEq(String teamName) {
        return StringUtils.hasText(teamName)? team.name.eq(teamName) : null;
    }

    private BooleanExpression ageGoe(Integer ageGoe) {
        return Objects.isNull(ageGoe) ? null : member.age.goe(ageGoe);
    }
    private BooleanExpression ageLoe(Integer ageLoe) {
        return Objects.isNull(ageLoe) ? null : member.age.loe(ageLoe);
    }
}
