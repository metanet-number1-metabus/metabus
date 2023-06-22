package com.metanet.metabus.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberInfoDto {
    private Long id;
    private String name;
    private String email;
    private String phoneNum;

}
