package com.metanet.metabus.member.dto;

import com.metanet.metabus.member.entity.Member;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberRegisterRequest {
    @NotBlank(message = "이메일을 입력해주세요.")
    @Email(message = "올바른 형식의 이메일 주소여야 합니다.")
    private String email;

    @NotBlank(message = "이름을 입력해주세요.")
    @Size(max = 20, message = "20글자 이내로 입력해 주세요.")
    private String name;


    @NotBlank(message = "패스워드를 입력해주세요.")
    @Pattern(regexp = "(?=.*[a-zA-Z])(?=.*[0-9]).{8,25}", message = "영문 숫자 조합 8자리 이상이여야 합니다.")
    private String password;

    @NotBlank(message = "핸드폰 번호를 입력해주세요.")
    @Pattern(regexp = "01[0|1|6|7|8|9]-\\d{3,4}-\\d{4}", message = "010-0000-0000 형식으로 입력해 주세요.")
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
