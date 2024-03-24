package com.inflearn.querydslstudy.repository;

import com.inflearn.querydslstudy.dto.MemberSearchCondition;
import com.inflearn.querydslstudy.dto.MemberTeamDto;
import com.inflearn.querydslstudy.dto.QMemberTeamDto;
import com.inflearn.querydslstudy.entity.Member;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

import static com.inflearn.querydslstudy.entity.QMember.member;
import static com.inflearn.querydslstudy.entity.QTeam.team;

/**
 * EntityManater를 제공해줘서 직접 주입받지 않을 수 있음.
 * getQuerydsl().applyPagination()을 이용해 페이징 정보를 세팅하지 않아도 됨.
 * 한계 존재함.
 * from()으로 시작
 * QueryFactory를 제공하지 않음.
 * 동적 바인딩 통한 Sort 기능에 버그 존재.
 */
public class MemberRepositoryImplWithSupport  extends QuerydslRepositorySupport     // Querydsl 구현체에게 편리성 제공하는 추상 클래스
        implements MemberRepositoryCustom {
    // EntityManager를 주입받아서 함께 사용할 수도 있긴 함.
    /**
     * Creates a new {@link QuerydslRepositorySupport} instance for the given domain type.
     */
    public MemberRepositoryImplWithSupport() {
        super(Member.class);
    }

    @Override
    public List<MemberTeamDto> searchByWhere(MemberSearchCondition condition) {
        return from(member)
                .leftJoin(member.team, team)
                .where(usernameEq(condition.getUsername()),
                        teamNameEq(condition.getTeamName()),
                        ageGoe(condition.getAgeGoe()),
                        ageLoe(condition.getAgeLoe()))
                .select(new QMemberTeamDto(
                        member.id.as("memberId"),
                        member.username,
                        member.age,
                        team.id.as("teamId"),
                        team.name.as("teamName")
                ))
                .fetch();
    }

    @Override
    public Page<MemberTeamDto> searchPageComplex(MemberSearchCondition condition, Pageable pageable) {
        JPQLQuery<MemberTeamDto> contentQuery = from(member)
                .leftJoin(member.team, team)
                .where(usernameEq(condition.getUsername()),
                        teamNameEq(condition.getTeamName()),
                        ageGoe(condition.getAgeGoe()),
                        ageLoe(condition.getAgeLoe()))
                .select(new QMemberTeamDto(
                        member.id.as("memberId"),
                        member.username,
                        member.age,
                        team.id.as("teamId"),
                        team.name.as("teamName")
                ));

        // Pagination 관련 정보인 offset(), limit()을 반영해줌.
        JPQLQuery<MemberTeamDto> pageQuery = getQuerydsl().applyPagination(pageable, contentQuery);
        QueryResults<MemberTeamDto> results = pageQuery.fetchResults();
        return new PageImpl<>(results.getResults(), pageable, results.getTotal());
    }

    @Override
    public Page<MemberTeamDto> searchPage_fetchCount(MemberSearchCondition condition, Pageable pageable) {
        return null;
    }

    @Override
    public Page<MemberTeamDto> searchPage_count(MemberSearchCondition condition, Pageable pageable) {
        return null;
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
