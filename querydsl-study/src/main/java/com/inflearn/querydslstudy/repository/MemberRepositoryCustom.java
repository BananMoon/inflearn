package com.inflearn.querydslstudy.repository;

import com.inflearn.querydslstudy.dto.MemberSearchCondition;
import com.inflearn.querydslstudy.dto.MemberTeamDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Querydsl 이용하는 사용자 정의 Interface
 */
public interface MemberRepositoryCustom {
    List<MemberTeamDto> searchByWhere(MemberSearchCondition condition);
    Page<MemberTeamDto> searchPageComplex(MemberSearchCondition condition, Pageable pageable);

    Page<MemberTeamDto> searchPage_fetchCount(MemberSearchCondition condition, Pageable pageable);
    Page<MemberTeamDto> searchPage_count(MemberSearchCondition condition, Pageable pageable);

}
