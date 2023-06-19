package com.metanet.metabus.member.service;

import com.metanet.metabus.common.exception.conflict.DuplicateEmailException;
import com.metanet.metabus.common.exception.not_found.AlreadyDeletedMemberException;
import com.metanet.metabus.common.exception.not_found.MemberNotFoundException;
import com.metanet.metabus.common.exception.unauthorized.InvalidPasswordException;
import com.metanet.metabus.member.dto.*;
import com.metanet.metabus.member.entity.Member;
import com.metanet.metabus.member.entity.Role;
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
        //이메일 중복
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

        //탈퇴한 회원
        memberRepository.findByEmailAndDeletedDateIsNotNull(memberLoginRequest.getEmail()).ifPresent(member1 -> {
            throw new AlreadyDeletedMemberException();
        });

        //비밀번호 불일치
        if (!passwordEncoder.matches(memberLoginRequest.getPassword(), member.getPassword()))
            throw new InvalidPasswordException();

        return MemberDto.builder()
                .id(member.getId())
                .name(member.getName())
                .email(memberLoginRequest.getEmail())
                .password(memberLoginRequest.getPassword())
                .role(member.getRole())
                .mileage(member.getMileage())
                .grade(member.getGrade())
                .phoneNum(member.getPhoneNum())
                .build();
    }

    @Transactional
    public MemberDto editInfo(MemberEditInfoRequest memberEditInfoRequest, MemberDto memberDto) { //이미 비밀번호 확인하고 들어가서 체크x
        String encoded = passwordEncoder.encode(memberDto.getPassword());

        Member member = Member.builder()
                .id(memberDto.getId())
                .email(memberEditInfoRequest.getEmail())
                .password(encoded)
                .name(memberEditInfoRequest.getName())
                .role(memberDto.getRole())
                .mileage(memberDto.getMileage())
                .grade(memberDto.getGrade())
                .phoneNum(memberEditInfoRequest.getPhoneNum())
                .build();

        memberRepository.save(member);

        return MemberDto.builder()
                .id(member.getId())
                .name(member.getName())
                .email(member.getEmail())
                .password(member.getPassword())
                .role(member.getRole())
                .mileage(member.getMileage())
                .grade(member.getGrade())
                .phoneNum(member.getPhoneNum())
                .build();
    }

    @Transactional
    public MemberDto editPassword(MemberPasswordRequest memberEditPasswordRequest, MemberDto memberDto) { //이미 비밀번호 확인하고 들어가서 체크x

        String encoded = passwordEncoder.encode(memberEditPasswordRequest.getPassword());

        Member member = Member.builder()
                .id(memberDto.getId())
                .email(memberDto.getEmail())
                .password(encoded)
                .name(memberDto.getName())
                .role(memberDto.getRole())
                .mileage(memberDto.getMileage())
                .grade(memberDto.getGrade())
                .phoneNum(memberDto.getPhoneNum())
                .build();

        memberRepository.save(member);

        return MemberDto.builder()
                .id(member.getId())
                .name(member.getName())
                .email(member.getEmail())
                .password(member.getPassword())
                .role(member.getRole())
                .mileage(member.getMileage())
                .grade(member.getGrade())
                .phoneNum(member.getPhoneNum())
                .build();
    }

    @Transactional
    public MemberDto editInfoOAuth(MemberOAuthRequest memberOAuthRequest, MemberDto memberDto) { //이미 비밀번호 확인하고 들어가서 체크x

        String encoded = passwordEncoder.encode(memberOAuthRequest.getPassword());

        Member member = Member.builder()
                .id(memberDto.getId())
                .email(memberDto.getEmail())
                .password(encoded)
                .name(memberDto.getName())
                .role(Role.USER)
                .mileage(memberDto.getMileage())
                .grade(memberDto.getGrade())
                .phoneNum(memberDto.getPhoneNum())
                .build();

        memberRepository.save(member);

        return MemberDto.builder()
                .id(member.getId())
                .name(member.getName())
                .email(member.getEmail())
                .password(member.getPassword())
                .role(member.getRole())
                .mileage(member.getMileage())
                .grade(member.getGrade())
                .phoneNum(member.getPhoneNum())
                .build();
    }

    @Transactional
    public void checkPwd(MemberLoginRequest memberLoginRequest, MemberDto memberDto) {
        Member member = memberRepository.findByEmail(memberDto.getEmail()).orElseThrow(MemberNotFoundException::new);

        //비밀번호 불일치
        if (!passwordEncoder.matches(memberLoginRequest.getPassword(), member.getPassword()))
            throw new InvalidPasswordException();
    }

    @Transactional
    public void delete(MemberDto memberDto) {
        Member member = memberRepository.findByEmail(memberDto.getEmail()).orElseThrow(MemberNotFoundException::new);

        member.delete();

        memberRepository.save(member);
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
