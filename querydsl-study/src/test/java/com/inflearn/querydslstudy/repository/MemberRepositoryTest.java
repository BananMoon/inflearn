package com.inflearn.querydslstudy.repository;

import com.inflearn.querydslstudy.dto.MemberSearchCondition;
import com.inflearn.querydslstudy.dto.MemberTeamDto;
import com.inflearn.querydslstudy.entity.Member;
import com.inflearn.querydslstudy.entity.Team;
import jakarta.persistence.EntityManager;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class MemberRepositoryTest {
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    EntityManager em;
    @Test
    void findById() {
        // given
        Member member = new Member("member1", 10);
        memberRepository.save(member);

        // when
        Member findMember = memberRepository.findById(member.getId()).get();

        // then
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember.getAge()).isEqualTo(member.getAge());
    }

    @Test
    void findAll() {
        // given
        Member member = new Member("member1", 10);
        memberRepository.save(member);

        // when
        List<Member> findMembers = memberRepository.findAll();
        // then
        assertThat(findMembers).hasSize(1);
        assertThat(findMembers).extracting("username", "age")
                .containsExactly(
                        new Tuple("member1", 10)
                );
    }
    @Test
    void findByUsername() {
        // given
        Member member = new Member("member1", 10);
        memberRepository.save(member);

        // when
        Member findMember = memberRepository.findByUsername(member.getUsername());

        // then
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember.getAge()).isEqualTo(member.getAge());
    }

    @Test
    void searchByWhere() {
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

        // when
        MemberSearchCondition condition = new MemberSearchCondition();
        condition.setTeamName("teamB");
        condition.setAgeGoe(10);
        condition.setAgeLoe(30);

        List<MemberTeamDto> result = memberRepository.searchByWhere(condition);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUsername()).isEqualTo(member3.getUsername());
        assertThat(result.get(0).getTeamName()).isEqualTo(member3.getTeam().getName());
        assertThat(result.get(0).getAge()).isEqualTo(member3.getAge());
    }
}