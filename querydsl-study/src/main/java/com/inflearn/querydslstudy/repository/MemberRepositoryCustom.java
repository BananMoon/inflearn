package com.inflearn.querydslstudy.repository;

import com.inflearn.querydslstudy.dto.MemberSearchCondition;
import com.inflearn.querydslstudy.dto.MemberTeamDto;

import java.util.List;

public interface MemberRepositoryCustom {
    List<MemberTeamDto> searchByWhere(MemberSearchCondition condition);
}
