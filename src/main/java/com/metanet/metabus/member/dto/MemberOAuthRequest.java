package com.metanet.metabus.member.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberOAuthRequest {
    @NotBlank(message = "이메일을 입력해주세요.")
    @Email(message = "올바른 형식의 이메일 주소여야 합니다.")
    private String email;

    @NotBlank(message = "이름을 입력해주세요.")
    private String name;

    @NotBlank(message = "패스워드를 입력해주세요.")
    @Pattern(regexp = "(?=.*[a-zA-Z])(?=.*[0-9]).{8,25}", message = "영문 숫자 조합 8자리 이상이여야 합니다.")
    private String password;
}
