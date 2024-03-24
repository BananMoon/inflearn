package com.inflearn.querydslstudy.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 필터링을 걸어서 Member를 조회
 */
@Getter
@Setter
public class MemberSearchCondition {
    // 회원명, 팀명, 나이 (ageGoe, ageLoe)
    private String username;
    private String teamName;
    private Integer ageGoe;
    private Integer ageLoe;
}
