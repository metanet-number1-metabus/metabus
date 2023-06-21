package com.metanet.metabus.member.dto;

import com.metanet.metabus.member.entity.Grade;
import com.metanet.metabus.member.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberInfoRequest {
    private Long memberId;
    private LocalDate createdDate;
    private LocalDate deletedDate;
    private String email;
    private Grade grade;
    private String name;
    private String phoneNum;
    private Role role;
}
