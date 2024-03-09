package com.inflearn.querydslstudy.repository;

import com.inflearn.querydslstudy.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA 를 이용한 Repository 버전
 */
public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

    Member findByUsername(String username);

}
