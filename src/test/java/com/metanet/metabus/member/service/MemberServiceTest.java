package com.metanet.metabus.member.service;

import com.metanet.metabus.common.exception.ErrorCode;
import com.metanet.metabus.common.exception.conflict.DuplicateEmailException;
import com.metanet.metabus.common.exception.not_found.AlreadyDeletedMemberException;
import com.metanet.metabus.common.exception.not_found.MemberNotFoundException;
import com.metanet.metabus.common.exception.unauthorized.InvalidPasswordException;
import com.metanet.metabus.member.dto.*;
import com.metanet.metabus.member.entity.Grade;
import com.metanet.metabus.member.entity.Member;
import com.metanet.metabus.member.entity.Role;
import com.metanet.metabus.member.repository.MemberRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MemberServiceTest {

    private final PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
    private final MemberRepository memberRepository = mock(MemberRepository.class);

    private final MemberService memberService = new MemberService(passwordEncoder, memberRepository);

    @Test
    @DisplayName("회원가입 성공")
    void register_success() {
        Member member = new Member(0L, "test", "12345678", "test@test.com", 0L, Role.USER, "010-0000-0000", Grade.ALPHA);

        when(memberRepository.findByEmail(member.getEmail())).thenReturn(Optional.of(member));
        when(memberRepository.save(any(Member.class))).thenReturn(member);

        Long result = Assertions.assertDoesNotThrow(() -> memberService.register(new MemberRegisterRequest()));
        assertEquals(result, member.getId());

    }

    @Test
    @DisplayName("회원가입 실패 - 이메일 중복")
    void register_fail() {
        Member member = new Member(0L, "test", "12345678", "test@test.com", 0L, Role.USER, "010-0000-0000", Grade.ALPHA);

        when(memberRepository.findByEmail(member.getEmail()))
                .thenReturn(Optional.of(member));

        DuplicateEmailException exception = Assertions.assertThrows(DuplicateEmailException.class, () -> {
            memberService.register(new MemberRegisterRequest("test@test.com", "test2", "12345678", "010-0000-0000"));
        });
        assertEquals(ErrorCode.DUPLICATED_EMAIL, exception.getErrorCode());
    }

    @Test
    @DisplayName("로그인 성공")
    void login_success() {
        Member member = new Member(0L, "test", "12345678", "test@test.com", 0L, Role.USER, "010-0000-0000", Grade.ALPHA);

        when(memberRepository.findByEmail(member.getEmail())).thenReturn(Optional.of(member));
        when(!passwordEncoder.matches(member.getPassword(), "12345678")).thenReturn(true);

        MemberDto result = Assertions.assertDoesNotThrow(() -> memberService.login(new MemberLoginRequest(member.getEmail(), member.getPassword())));

        assertEquals(result.getId(), member.getId());
        assertEquals(result.getName(), member.getName());
        assertEquals(result.getPassword(), member.getPassword());
        assertEquals(result.getEmail(), member.getEmail());
        assertEquals(result.getMileage(), member.getMileage());
        assertEquals(result.getRole(), member.getRole());
        assertEquals(result.getPhoneNum(), member.getPhoneNum());

    }

    @Test
    @DisplayName("로그인 실패(1) - 존재하지 않는 유저")
    void login_fail() {
        Member member = new Member(0L, "test", "12345678", "test@test.com", 0L, Role.USER, "010-0000-0000", Grade.ALPHA);
        when(memberRepository.findByEmail(member.getEmail())).thenReturn(Optional.of(member));

        MemberNotFoundException exception = Assertions.assertThrows(MemberNotFoundException.class, () -> {
            memberService.login(new MemberLoginRequest("test2@test.com", "12345678"));
        });

        assertEquals(ErrorCode.MEMBER_NOT_FOUND, exception.getErrorCode());

    }

    @Test
    @DisplayName("로그인 실패(2) - 이미 탈퇴한 회원인 경우")
    void login_fail2() {
        Member member = new Member(0L, "test", "12345678", "test@test.com", 0L, Role.USER, "010-0000-0000", Grade.ALPHA);

        when(memberRepository.findByEmail(member.getEmail())).thenReturn(Optional.of(member));
        when(memberRepository.findByEmailAndDeletedDateIsNotNull(member.getEmail())).thenReturn(Optional.of(member));

        AlreadyDeletedMemberException exception = Assertions.assertThrows(AlreadyDeletedMemberException.class, () -> {
            memberService.login(new MemberLoginRequest("test@test.com", "12345678"));
        });

        assertEquals(ErrorCode.ALREADY_DELETED_MEMBER, exception.getErrorCode());
    }

    @Test
    @DisplayName("로그인 실패(3) - 비밀번호 불일치")
    void login_fail3() {
        Member member = new Member(0L, "test", "12345678", "test@test.com", 0L, Role.USER, "010-0000-0000", Grade.ALPHA);

        when(memberRepository.findByEmail(member.getEmail())).thenReturn(Optional.of(member));
        when(!passwordEncoder.matches(member.getPassword(), "123456788")).thenReturn(false);

        InvalidPasswordException exception = Assertions.assertThrows(InvalidPasswordException.class, () -> {
            memberService.login(new MemberLoginRequest("test@test.com", "12345678"));
        });

        assertEquals(ErrorCode.INVALID_PASSWORD, exception.getErrorCode());
    }

    @Test
    @DisplayName("회원 정보 수정(info) 성공")
    void editInfo_success() {
        Member member = new Member(0L, "test", "12345678", "test@test.com", 0L, Role.USER, "010-0000-0000", Grade.ALPHA);
        MemberEditInfoRequest memberEditInfoRequest = new MemberEditInfoRequest("test@test.com", "test_edit", "010-1111-1111");
        String encoded = "EhATgL83Pc6";

        when(memberRepository.save(any(Member.class))).thenReturn(member);
        when(passwordEncoder.encode(any())).thenReturn(encoded);

        MemberDto result = Assertions.assertDoesNotThrow(() ->
                memberService.editInfo(memberEditInfoRequest, new MemberDto(member.getId(), member.getName(), member.getPassword(), member.getEmail(), member.getMileage(), member.getRole(), member.getPhoneNum())));

        assertEquals(result.getId(), member.getId());
        assertEquals(result.getName(), memberEditInfoRequest.getName());
        assertEquals(result.getPassword(), encoded);
        assertEquals(result.getEmail(), memberEditInfoRequest.getEmail());
        assertEquals(result.getMileage(), member.getMileage());
        assertEquals(result.getRole(), member.getRole());
        assertEquals(result.getPhoneNum(), memberEditInfoRequest.getPhoneNum());
    }

    @Test
    @DisplayName("비밀번호 수정 성공")
    void editPassword_success() {
        Member member = new Member(0L, "test", "12345678", "test@test.com", 0L, Role.USER, "010-0000-0000", Grade.ALPHA);
        MemberPasswordRequest memberPasswordRequest = new MemberPasswordRequest("12345678910");
        String encoded = "EhATgL83Pc6";

        when(memberRepository.save(any(Member.class))).thenReturn(member);
        when(passwordEncoder.encode(memberPasswordRequest.getPassword())).thenReturn(encoded);

        MemberDto result = Assertions.assertDoesNotThrow(() ->
                memberService.editPassword(memberPasswordRequest, new MemberDto(member.getId(), member.getName(), member.getPassword(), member.getEmail(), member.getMileage(), member.getRole(), member.getPhoneNum())));

        assertEquals(result.getId(), member.getId());
        assertEquals(result.getName(), member.getName());
        assertEquals(result.getPassword(), encoded);
        assertEquals(result.getEmail(), member.getEmail());
        assertEquals(result.getMileage(), member.getMileage());
        assertEquals(result.getRole(), member.getRole());
        assertEquals(result.getPhoneNum(), member.getPhoneNum());
    }

    @Test
    @DisplayName("회원 정보 수정(oauth) 성공")
    void editInfoOAuth_success() {
        Member member = new Member(0L, "test", "12345678", "test@test.com", 0L, Role.USER, "010-0000-0000", Grade.ALPHA);
        MemberOAuthRequest memberOAuthRequest = new MemberOAuthRequest("test@test.com", "test", "12345678");
        String encoded = "EhATgL83Pc6";

        when(memberRepository.save(any(Member.class))).thenReturn(member);
        when(passwordEncoder.encode(memberOAuthRequest.getPassword())).thenReturn(encoded);

        MemberDto result = Assertions.assertDoesNotThrow(() ->
                memberService.editInfoOAuth(memberOAuthRequest, new MemberDto(member.getId(), member.getName(), member.getPassword(), member.getEmail(), member.getMileage(), member.getRole(), member.getPhoneNum())));

        assertEquals(result.getId(), member.getId());
        assertEquals(result.getName(), memberOAuthRequest.getName());
        assertEquals(result.getPassword(), encoded);
        assertEquals(result.getEmail(), memberOAuthRequest.getEmail());
        assertEquals(result.getMileage(), member.getMileage());
        assertEquals(result.getRole(), member.getRole());
        assertEquals(result.getPhoneNum(), member.getPhoneNum());
    }

    @Test
    @DisplayName("비밀번호 확인 성공")
    void checkPwd_success() {
        Member member = new Member(0L, "test", "12345678", "test@test.com", 0L, Role.USER, "010-0000-0000", Grade.ALPHA);

        when(memberRepository.findByEmail(member.getEmail())).thenReturn(Optional.of(member));
        when(!passwordEncoder.matches(member.getPassword(), "12345678")).thenReturn(true);

        Assertions.assertDoesNotThrow(() -> memberService.checkPwd(new MemberLoginRequest(member.getEmail(), member.getPassword()), new MemberDto(member.getId(), member.getName(), member.getPassword(), member.getEmail(), member.getMileage(), member.getRole(), member.getPhoneNum())));

    }

    @Test
    @DisplayName("비밀번호 확인 실패(1) - 존재하지 않는 유저")
    void checkPwd_fail() {
        Member member = new Member(0L, "test", "12345678", "test@test.com", 0L, Role.USER, "010-0000-0000", Grade.ALPHA);
        when(memberRepository.findByEmail(member.getEmail())).thenReturn(Optional.of(member));

        MemberNotFoundException exception = Assertions.assertThrows(MemberNotFoundException.class, () -> {
            memberService.checkPwd(new MemberLoginRequest("test2@test.com", "12345678"), new MemberDto());
        });

        assertEquals(ErrorCode.MEMBER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("비밀번호 확인 실패(2) - 비밀번호 불일치")
    void checkPwd_fail2() {
        Member member = new Member(0L, "test", "12345678", "test@test.com", 0L, Role.USER, "010-0000-0000", Grade.ALPHA);

        when(memberRepository.findByEmail(member.getEmail())).thenReturn(Optional.of(member));
        when(!passwordEncoder.matches(member.getPassword(), "123456788")).thenReturn(false);

        InvalidPasswordException exception = Assertions.assertThrows(InvalidPasswordException.class, () -> {
            memberService.checkPwd(new MemberLoginRequest("test@test.com", "12345678"), new MemberDto(member.getId(), member.getName(), member.getPassword(), member.getEmail(), member.getMileage(), member.getRole(), member.getPhoneNum()));
        });

        assertEquals(ErrorCode.INVALID_PASSWORD, exception.getErrorCode());
    }

    @Test
    @DisplayName("회원 삭제 성공")
    void delete_success() {
        Member member = new Member(0L, "test", "12345678", "test@test.com", 0L, Role.USER, "010-0000-0000", Grade.ALPHA);

        when(memberRepository.findByEmail(member.getEmail())).thenReturn(Optional.of(member));
        when(memberRepository.save(any(Member.class))).thenReturn(member);

        Assertions.assertDoesNotThrow(() -> memberService.delete(new MemberDto(member.getId(), member.getName(), member.getPassword(), member.getEmail(), member.getMileage(), member.getRole(), member.getPhoneNum())));
    }

    @Test
    @DisplayName("회원 삭제 실패(1) - 존재하지 않는 유저")
    void delete_fail() {
        Member member = new Member(0L, "test", "12345678", "test@test.com", 0L, Role.USER, "010-0000-0000", Grade.ALPHA);
        when(memberRepository.findByEmail(member.getEmail())).thenReturn(Optional.of(member));

        MemberNotFoundException exception = Assertions.assertThrows(MemberNotFoundException.class, () -> {
            memberService.delete(new MemberDto());
        });

        assertEquals(ErrorCode.MEMBER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("유효성 검사 성공")
    void validateHandling_success() {

        BindingResult bindingResult = mock(BindingResult.class);

        FieldError fieldError1 = new FieldError("objectName", "email", "이메일을 입력해주세요.");
        FieldError fieldError2 = new FieldError("objectName", "name", "이름을 입력해주세요.");

        List<FieldError> fieldErrors = new ArrayList<>();
        fieldErrors.add(fieldError1);
        fieldErrors.add(fieldError2);

        when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);

        Map<String, String> validatorResult = memberService.validateHandling(bindingResult);

        assertEquals(2, validatorResult.size());
        assertEquals("이메일을 입력해주세요.", validatorResult.get("valid_email"));
        assertEquals("이름을 입력해주세요.", validatorResult.get("valid_name"));
    }
}