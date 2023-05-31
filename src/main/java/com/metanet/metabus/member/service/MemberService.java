package com.metanet.metabus.member.service;

import com.metanet.metabus.common.exception.conflict.DuplicateEmailException;
import com.metanet.metabus.common.exception.not_found.AlreadyDeletedMemberException;
import com.metanet.metabus.common.exception.not_found.MemberNotFoundException;
import com.metanet.metabus.common.exception.unauthorized.InvalidPasswordException;
import com.metanet.metabus.member.dto.MemberDto;
import com.metanet.metabus.member.dto.MemberLoginRequest;
import com.metanet.metabus.member.dto.MemberRegisterRequest;
import com.metanet.metabus.member.entity.Member;
import com.metanet.metabus.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    @Transactional
    public Long register(MemberRegisterRequest memberRegisterRequest) {
        memberRepository.findByEmail(memberRegisterRequest.getEmail()).ifPresent(member -> {
            throw new DuplicateEmailException();
        });

        String encoded = passwordEncoder.encode(memberRegisterRequest.getPassword());
        Member member = memberRegisterRequest.toEntity(encoded);
        return memberRepository.save(member).getId();
    }

    @Transactional
    public MemberDto login(MemberLoginRequest memberLoginRequest) {
        Member member = memberRepository.findByEmail(memberLoginRequest.getEmail()).orElseThrow(MemberNotFoundException::new);
        if (!member.isEnabled() && member.getDeletedDate() != null) throw new AlreadyDeletedMemberException();
        if (!passwordEncoder.matches(memberLoginRequest.getPassword(), member.getPassword()))
            throw new InvalidPasswordException();

        return MemberDto.builder()
                .name(member.getName())
                .email(memberLoginRequest.getEmail())
                .password(memberLoginRequest.getPassword())
                .role(member.getRole())
                .phoneNum(member.getPhoneNum())
                .build();
    }

    @Transactional
    public Map<String, String> validateHandling(BindingResult bindingResult) {
        Map<String, String> validatorResult = new HashMap<>();

        for (FieldError error : bindingResult.getFieldErrors()) {
            String validKeyName = String.format("valid_%s", error.getField());
            validatorResult.put(validKeyName, error.getDefaultMessage());
        }
        return validatorResult;
    }
}
