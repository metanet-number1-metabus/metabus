package com.metanet.metabus.member.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberPasswordRequest {
    @NotBlank(message = "패스워드를 입력해주세요.")
    @Pattern(regexp = "(?=.*[a-zA-Z])(?=.*[0-9]).{8,25}", message = "영문 숫자 조합 8자리 이상이여야 합니다.")
    private String password;
}