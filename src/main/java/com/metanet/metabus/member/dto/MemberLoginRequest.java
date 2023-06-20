package com.metanet.metabus.member.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberLoginRequest {
    @NotBlank(message = "이메일을 입력해주세요.")
    @Email(message = "올바른 형식의 이메일 주소여야 합니다.")
    private String email;

    @NotBlank(message = "패스워드를 입력해주세요.")
    private String password;
}
