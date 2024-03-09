package com.inflearn.querydslstudy.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)  // JPA는 필수로 필요로 하며 PROTECTED까지 허용함.
@ToString(of = {"id", "username", "age"})           // 연관관계인 team은 추가하면 안됨. 무한루프 발생함.
public class Member {
    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;
    private String username;
    private int age;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")   // name : FK 칼럼명
    private Team team;              // 연관관계의 주인. 데이터베이스 외래키값 변경

    public Member(String username) {
        this(username, 0);
    }
    public Member(String username, int age) {
        this(username, age, null);
    }
    public Member(String username, int age, Team team) {
        this.username = username;
        this.age = age;
        if (team != null) {
            changeTeam(team);
        }
    }

    /**
     * 연관관계 편의 메소드 : 양방향 연관관계 한번에 처리
     * @param team
     */
    public void changeTeam(Team team) {
        this.team = team;
        team.getMembers().add(this);    // 양방향 연관관계이므로
    }
}