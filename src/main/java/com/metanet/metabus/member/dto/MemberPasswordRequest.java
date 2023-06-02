package com.metanet.metabus.member.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberPasswordRequest {
    @NotBlank(message = "패스워드를 입력해주세요.")
    @Size(min = 8, message = "최소 8자리 이상의 패스워드를 입력해 주세요.")
    private String password;
}