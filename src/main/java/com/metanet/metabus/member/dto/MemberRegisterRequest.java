package com.metanet.metabus.member.dto;

import com.metanet.metabus.member.entity.Member;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberRegisterRequest {
    @Email(message = "올바른 형식의 이메일 주소여야 합니다.")
    private String email;

    @NotBlank(message = "이름을 입력해주세요.")
    private String name;

    @NotBlank(message = "패스워드를 입력해주세요.")
    @Size(min = 8, message = "최소 8자리 이상의 패스워드를 입력해 주세요.")
    private String password;

    @NotBlank(message = "핸드폰 번호를 입력해주세요.")
    private String phoneNum;

    public Member toEntity(String encoder) {
        return Member.builder()
                .email(this.email)
                .name(this.name)
                .password(encoder)
                .phoneNum(this.phoneNum)
                .build();

    }
}
