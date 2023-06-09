package com.metanet.metabus.member.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberEditInfoRequest {
    @NotBlank(message = "이메일을 입력해주세요.")
    @Email(message = "올바른 형식의 이메일 주소여야 합니다.")
    private String email;

    @NotBlank(message = "이름을 입력해주세요.")
    private String name;

    @NotBlank(message = "핸드폰 번호를 입력해주세요.")
    @Pattern(regexp = "/^01([0|1|6|7|8|9])-\\d{3,4}-\\d{4}$/", message = "올바른 형식의 핸드폰 번호여야 합니다.")
    private String phoneNum;
}
