package com.metanet.metabus.member.entity;

import com.metanet.metabus.common.BaseEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@NoArgsConstructor
@Getter
@Table
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private Long mileage = 0L;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    @Column(nullable = false)
    private String phoneNum;

    @Enumerated(EnumType.STRING)
    private Grade grade = Grade.ALPHA;

    @Builder
    public Member(Long id, String name, String password, String email, Long mileage, Role role, String phoneNum, Grade grade) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.email = email;
        this.mileage = 0L;
        this.role = role == null ? Role.USER : role;
        this.phoneNum = phoneNum;
        this.grade = grade == null ? Grade.ALPHA : grade;
    }

    public void updateMileage(Long mileage) {
        this.mileage = mileage;
    }

}
