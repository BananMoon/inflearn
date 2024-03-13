package com.inflearn.querydslstudy.controller;

import com.inflearn.querydslstudy.dto.MemberSearchCondition;
import com.inflearn.querydslstudy.dto.MemberTeamDto;
import com.inflearn.querydslstudy.repository.MemberJpaRepository;
import com.inflearn.querydslstudy.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberJpaRepository memberJpaRepository;
    private final MemberRepository memberRepository;

    @GetMapping("/v1/members")
    public List<MemberTeamDto> getMembers(MemberSearchCondition condition) {    // QueryParmeter는 별도의 애노테이션 X
        return memberJpaRepository.searchByWhere(condition);
    }

    @GetMapping("/v2/members")
    public Page<MemberTeamDto> getMembersPage_deprecated(MemberSearchCondition condition, Pageable pageable) {
        return memberRepository.searchPage_fetchCount(condition,pageable);
    }
    @GetMapping("/v3/members")
    public Page<MemberTeamDto> getMembersPage(MemberSearchCondition condition, Pageable pageable) {
        return memberRepository.searchPage_count(condition,pageable);
    }
}
