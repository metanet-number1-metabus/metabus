package com.metanet.metabus.member.dto;

import com.metanet.metabus.member.entity.Grade;
import com.metanet.metabus.member.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberDto {
    private Long id;
    private String name;
    private String password;
    private String email;
    private Long mileage;
    private Role role;
    private String phoneNum;
    private Grade grade;
}
