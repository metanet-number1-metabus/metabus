package com.metanet.metabus.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.metanet.metabus.bus.entity.Reservation;
import com.metanet.metabus.common.exception.conflict.DuplicateEmailException;
import com.metanet.metabus.common.exception.not_found.AlreadyDeletedMemberException;
import com.metanet.metabus.common.exception.unauthorized.InvalidPasswordException;
import com.metanet.metabus.member.dto.*;
import com.metanet.metabus.member.entity.Grade;
import com.metanet.metabus.member.entity.Role;
import com.metanet.metabus.member.service.MemberService;
import com.metanet.metabus.member.service.MyPageService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.BindingResult;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MemberController.class)
@WithMockUser
class MemberControllerTest {

    @MockBean
    MemberService memberService;

    @MockBean
    MyPageService myPageService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;


    /**
     * 회원 가입
     */
    @Test
    @DisplayName("회원가입 GET 성공")
    void get_register() throws Exception {

        mockMvc.perform(get("/member/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("log/register"))
                .andExpect(model().attributeExists("memberRegisterRequest"));
    }

    @Test
    @DisplayName("회원가입 POST 성공 - 유효성 검사 통과")
    void post_register() throws Exception {

        MemberRegisterRequest memberRegisterRequest = new MemberRegisterRequest("test@test.com", "test", "12345678", "010-0000-0000");

        given(memberService.register(any(MemberRegisterRequest.class))).willReturn(0L);

        mockMvc.perform(post("/member/register")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", memberRegisterRequest.getEmail())
                        .param("name", memberRegisterRequest.getName())
                        .param("password", memberRegisterRequest.getPassword())
                        .param("phoneNum", memberRegisterRequest.getPhoneNum())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection()) // 리다이렉션 상태 코드
                .andExpect(redirectedUrl("/")); // 리다이렉션 URL

        verify(memberService).register(any(MemberRegisterRequest.class));
    }

    @Test
    @DisplayName("회원가입 POST 실패(1) - 중복된 이메일(1)")
    void post_register_fail() throws Exception {

        given(memberService.register(any(MemberRegisterRequest.class))).willThrow(new DuplicateEmailException());

        MemberRegisterRequest memberRegisterRequest = new MemberRegisterRequest("test@test.com", "test", "12345678", "010-0000-0000");

        mockMvc.perform(post("/member/register")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", memberRegisterRequest.getEmail())
                        .param("name", memberRegisterRequest.getName())
                        .param("password", memberRegisterRequest.getPassword())
                        .param("phoneNum", memberRegisterRequest.getPhoneNum())
                        .with(csrf()))
                .andExpect(status().isConflict())
                .andExpect(content().string(equalTo("이미 사용중인 이메일입니다.")));

        verify(memberService).register(any(MemberRegisterRequest.class));
    }

    @Test
    @DisplayName("회원가입 POST 실패(2) - 중복된 이메일(2)")
    void post_register_fail2() throws Exception {

        given(memberService.emailCheck(any(String.class))).willReturn(true);

        MemberRegisterRequest memberRegisterRequest = new MemberRegisterRequest("test@test.com", "test", "12345678", "010-0000-0000");

        mockMvc.perform(post("/member/register")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", memberRegisterRequest.getEmail())
                        .param("name", memberRegisterRequest.getName())
                        .param("password", memberRegisterRequest.getPassword())
                        .param("phoneNum", memberRegisterRequest.getPhoneNum())
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("log/register"));
    }

    @Test
    @DisplayName("회원가입 POST 실패(3) - 유효성 검사 실패(빈 값 입력)")
    void post_register_fail3() throws Exception {

        Map<String, String> validatorResult = new HashMap<>();

        validatorResult.put("valid_email", "이메일을 입력해주세요.");
        validatorResult.put("valid_name", "이름을 입력해주세요.");
        validatorResult.put("valid_password", "패스워드를 입력해주세요.");
        validatorResult.put("valid_phoneNum", "핸드폰 번호를 입력해주세요.");

        given(memberService.validateHandling(any(BindingResult.class))).willReturn(validatorResult);

        MemberRegisterRequest memberRegisterRequest = new MemberRegisterRequest(" ", " ", " ", " ");

        mockMvc.perform(post("/member/register")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", memberRegisterRequest.getEmail())
                        .param("name", memberRegisterRequest.getName())
                        .param("password", memberRegisterRequest.getPassword())
                        .param("phoneNum", memberRegisterRequest.getPhoneNum())
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("log/register"));

        verify(memberService).validateHandling(any(BindingResult.class));
    }

    /**
     * 로그인
     */
    @Test
    @DisplayName("로그인 GET 성공")
    void get_login() throws Exception {

        mockMvc.perform(get("/member/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("log/login"))
                .andExpect(model().attributeExists("memberLoginRequest"));
    }

    @Test
    @DisplayName("로그인 POST 성공 - 유효성 검사 통과")
    void post_login() throws Exception {

        MemberLoginRequest memberLoginRequest = new MemberLoginRequest("test@test.com", "12345678");

        given(memberService.login(any(MemberLoginRequest.class))).willReturn(any(MemberDto.class));

        mockMvc.perform(post("/member/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", memberLoginRequest.getEmail())
                        .param("password", memberLoginRequest.getPassword())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection()) // 리다이렉션 상태 코드
                .andExpect(redirectedUrl("/")); // 리다이렉션 URL

        verify(memberService).login(any(MemberLoginRequest.class));
    }

    @Test
    @DisplayName("로그인 POST 실패(1) - 탈퇴한 회원")
    void post_login_fail() throws Exception {

        given(memberService.login(any(MemberLoginRequest.class))).willThrow(new AlreadyDeletedMemberException());

        MemberRegisterRequest memberRegisterRequest = new MemberRegisterRequest("test@test.com", "test", "12345678", "010-0000-0000");

        mockMvc.perform(post("/member/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", memberRegisterRequest.getEmail())
                        .param("password", memberRegisterRequest.getPassword())
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(content().string(equalTo("이미 탈퇴된 계정입니다.")));

        verify(memberService).login(any(MemberLoginRequest.class));
    }

    @Test
    @DisplayName("로그인 POST 실패(2) - 탈퇴한 회원(2)")
    void post_login_fail2() throws Exception {

        given(memberService.deleteMemberCheck(any(MemberLoginRequest.class))).willReturn(true);

        MemberLoginRequest memberLoginRequest = new MemberLoginRequest("test@test.com", "12345678");

        mockMvc.perform(post("/member/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", memberLoginRequest.getEmail())
                        .param("password", memberLoginRequest.getPassword())
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("log/login"));

        verify(memberService).deleteMemberCheck(any(MemberLoginRequest.class));
    }

    @Test
    @DisplayName("로그인 POST 실패(3) - 비밀번호 불일치")
    void post_login_fail3() throws Exception {

        given(memberService.login(any(MemberLoginRequest.class))).willThrow(new InvalidPasswordException());

        MemberRegisterRequest memberRegisterRequest = new MemberRegisterRequest("test@test.com", "test", "12345678", "010-0000-0000");

        mockMvc.perform(post("/member/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", memberRegisterRequest.getEmail())
                        .param("password", memberRegisterRequest.getPassword())
                        .with(csrf()))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(equalTo("잘못된 패스워드입니다.")));

        verify(memberService).login(any(MemberLoginRequest.class));
    }

    @Test
    @DisplayName("로그인 POST 실패(4) - 비밀번호 불일치(2)")
    void post_login_fail4() throws Exception {

        given(memberService.passwordCheck(any(MemberLoginRequest.class))).willReturn(true);

        MemberRegisterRequest memberRegisterRequest = new MemberRegisterRequest("test@test.com", "test", "12345678", "010-0000-0000");

        mockMvc.perform(post("/member/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", memberRegisterRequest.getEmail())
                        .param("password", memberRegisterRequest.getPassword())
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("log/login"));

        verify(memberService).passwordCheck(any(MemberLoginRequest.class));
    }

    @Test
    @DisplayName("로그인 POST 실패(5) - 유효성 검사 실패(빈 값 입력)")
    void post_login_fail5() throws Exception {

        Map<String, String> validatorResult = new HashMap<>();

        validatorResult.put("valid_email", "이메일을 입력해주세요.");
        validatorResult.put("valid_password", "패스워드를 입력해주세요.");

        given(memberService.validateHandling(any(BindingResult.class))).willReturn(validatorResult);

        MemberRegisterRequest memberRegisterRequest = new MemberRegisterRequest(" ", " ", " ", " ");

        mockMvc.perform(post("/member/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", memberRegisterRequest.getEmail())
                        .param("password", memberRegisterRequest.getPassword())
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("log/login"));

        verify(memberService).validateHandling(any(BindingResult.class));
    }

    /**
     * 로그아웃
     */
    @Test
    @DisplayName("로그아웃 POST 성공 - 세션 O")
    void post_logout() throws Exception {

        MemberDto memberDto = new MemberDto(0L, "test", "12345678", "test@test.com", 0L, Role.USER, "010-0000-0000", Grade.ALPHA);
        MockHttpSession mockHttpSession = new MockHttpSession();
        mockHttpSession.setAttribute(SessionConst.LOGIN_MEMBER, memberDto);

        mockMvc.perform(post("/member/logout")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .session(mockHttpSession)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection()) // 리다이렉션 상태 코드
                .andExpect(redirectedUrl("/"))
                .andExpect(request().sessionAttribute(SessionConst.LOGIN_MEMBER, nullValue()));

        assertTrue(mockHttpSession.isInvalid());
    }

    @Test
    @DisplayName("로그아웃 POST 실패 - 세션 X")
    void post_logout_fail() throws Exception {

        MockHttpSession mockHttpSession = new MockHttpSession();

        mockMvc.perform(post("/member/logout")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .session(mockHttpSession)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection()) // 리다이렉션 상태 코드
                .andExpect(redirectedUrl("/")); // 리다이렉션 URL
    }

    /**
     * 비밀번호 체크
     */
    @Test
    @DisplayName("비밀번호 체크 GET 성공(1) - info")
    void get_checkPwd_success() throws Exception {
        MemberDto memberDto = new MemberDto(0L, "test", "12345678", "test@test.com", 0L, Role.USER, "010-0000-0000", Grade.ALPHA);
        MockHttpSession mockHttpSession = new MockHttpSession();
        mockHttpSession.setAttribute(SessionConst.LOGIN_MEMBER, memberDto);

        mockMvc.perform(get("/member/check/{url}", "info")
                        .session(mockHttpSession))
                .andExpect(status().isOk())
                .andExpect(view().name("log/check_password_info"))
                .andExpect(model().attributeExists("memberLoginRequest"))
                .andExpect(model().attributeExists("original"));
    }

    @Test
    @DisplayName("비밀번호 체크 GET 성공(2) - pwd")
    void get_checkPwd_success2() throws Exception {
        MemberDto memberDto = new MemberDto(0L, "test", "12345678", "test@test.com", 0L, Role.USER, "010-0000-0000", Grade.ALPHA);
        MockHttpSession mockHttpSession = new MockHttpSession();
        mockHttpSession.setAttribute(SessionConst.LOGIN_MEMBER, memberDto);

        mockMvc.perform(get("/member/check/{url}", "pwd")
                        .session(mockHttpSession))
                .andExpect(status().isOk())
                .andExpect(view().name("log/check_password_pwd"))
                .andExpect(model().attributeExists("memberLoginRequest"))
                .andExpect(model().attributeExists("original"));
    }

    @Test
    @DisplayName("비밀번호 체크 GET 성공(3) - delete")
    void get_checkPwd_success3() throws Exception {
        MemberDto memberDto = new MemberDto(0L, "test", "12345678", "test@test.com", 0L, Role.USER, "010-0000-0000", Grade.ALPHA);
        MockHttpSession mockHttpSession = new MockHttpSession();
        mockHttpSession.setAttribute(SessionConst.LOGIN_MEMBER, memberDto);

        mockMvc.perform(get("/member/check/{url}", "delete")
                        .session(mockHttpSession))
                .andExpect(status().isOk())
                .andExpect(view().name("log/check_password_delete"))
                .andExpect(model().attributeExists("memberLoginRequest"))
                .andExpect(model().attributeExists("original"));
    }

    @Test
    @DisplayName("비밀번호 체크 GET 실패(1) - 잘못된 url 요청")
    void get_checkPwd_fail() throws Exception {
        MemberDto memberDto = new MemberDto(0L, "test", "12345678", "test@test.com", 0L, Role.USER, "010-0000-0000", Grade.ALPHA);
        MockHttpSession mockHttpSession = new MockHttpSession();
        mockHttpSession.setAttribute(SessionConst.LOGIN_MEMBER, memberDto);

        mockMvc.perform(get("/member/check/{url}", "error")
                        .session(mockHttpSession))
                .andExpect(status().isOk())
                .andExpect(view().name("/error/404"));
    }

    @Test
    @DisplayName("비밀번호 체크 GET 실패(2) - 세션 X")
    void get_checkPwd_fail2() throws Exception {
        MockHttpSession mockHttpSession = new MockHttpSession();

        mockMvc.perform(get("/member/check/{url}", "info")
                        .session(mockHttpSession))
                .andExpect(status().is3xxRedirection()) // 리다이렉션 상태 코드
                .andExpect(redirectedUrl("/member/login")); // 리다이렉션 URL
    }

    @Test
    @DisplayName("비밀번호 체크 GET 실패(3) - Role이 GUEST 일 때")
    void get_checkPwd_fail3() throws Exception {
        MemberDto memberDto = new MemberDto(0L, "test", "12345678", "test@test.com", 0L, Role.GUEST, "010-0000-0000", Grade.ALPHA);
        MockHttpSession mockHttpSession = new MockHttpSession();
        mockHttpSession.setAttribute(SessionConst.LOGIN_MEMBER, memberDto);

        mockMvc.perform(get("/member/check/{url}", "info")
                        .session(mockHttpSession))
                .andExpect(status().is3xxRedirection()) // 리다이렉션 상태 코드
                .andExpect(redirectedUrl("/member/edit/oauth")); // 리다이렉션 URL
    }

    @Test
    @DisplayName("비밀번호 체크 GET 실패(3) - 잘못된 password")
    void post_checkPwd_fail4() throws Exception {

        given(memberService.login(any(MemberLoginRequest.class))).willThrow(new InvalidPasswordException());

        MemberRegisterRequest memberRegisterRequest = new MemberRegisterRequest("test@test.com", "test", "12345678", "010-0000-0000");

        mockMvc.perform(post("/member/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", memberRegisterRequest.getEmail())
                        .param("password", memberRegisterRequest.getPassword())
                        .with(csrf()))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(equalTo("잘못된 패스워드입니다.")));

        verify(memberService).login(any(MemberLoginRequest.class));
    }

    @Test
    @DisplayName("비밀번호 체크(info) POST 성공 - 유효성 검사 통과")
    void post_checkPwdInfo() throws Exception {

        MemberDto memberDto = new MemberDto(0L, "test", "12345678", "test@test.com", 0L, Role.USER, "010-0000-0000", Grade.ALPHA);
        MockHttpSession mockHttpSession = new MockHttpSession();
        mockHttpSession.setAttribute(SessionConst.LOGIN_MEMBER, memberDto);

        MemberLoginRequest memberLoginRequest = new MemberLoginRequest("test@test.com", "12345678");

        mockMvc.perform(post("/member/check/info")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", memberLoginRequest.getEmail())
                        .param("password", memberLoginRequest.getPassword())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection()) // 리다이렉션 상태 코드
                .andExpect(redirectedUrl("/member/edit/info")); // 리다이렉션 URL
    }

    @Test
    @DisplayName("비밀번호 체크(info) POST 실패(1) - 유효성 검사 실패(빈 값 입력)")
    void post_checkPwdInfo_fail() throws Exception {

        MemberDto memberDto = new MemberDto(0L, "test", "12345678", "test@test.com", 0L, Role.USER, "010-0000-0000", Grade.ALPHA);
        MockHttpSession mockHttpSession = new MockHttpSession();
        mockHttpSession.setAttribute(SessionConst.LOGIN_MEMBER, memberDto);

        Map<String, String> validatorResult = new HashMap<>();

        validatorResult.put("valid_email", "이메일을 입력해주세요.");
        validatorResult.put("valid_password", "패스워드를 입력해주세요.");

        given(memberService.validateHandling(any(BindingResult.class))).willReturn(validatorResult);

        MemberLoginRequest memberLoginRequest = new MemberLoginRequest(" ", " ");

        mockMvc.perform(post("/member/check/info")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", memberLoginRequest.getEmail())
                        .param("password", memberLoginRequest.getPassword())
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("log/check_password_info"));
    }

    @Test
    @DisplayName("비밀번호 체크(info) POST 실패(2) - 잘못된 password")
    void post_checkPwdInfo_fail2() throws Exception {

        given(memberService.passwordCheck(any(MemberLoginRequest.class))).willReturn(true);

        MemberLoginRequest memberLoginRequest = new MemberLoginRequest("test@test.com", "12345678");

        mockMvc.perform(post("/member/check/info")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", memberLoginRequest.getEmail())
                        .param("password", memberLoginRequest.getPassword())
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("log/check_password_info"));

        verify(memberService).passwordCheck(any(MemberLoginRequest.class));
    }

    @Test
    @DisplayName("비밀번호 체크(pwd) POST 성공 - 유효성 검사 통과")
    void post_checkPwdPwd() throws Exception {

        MemberDto memberDto = new MemberDto(0L, "test", "12345678", "test@test.com", 0L, Role.USER, "010-0000-0000", Grade.ALPHA);
        MockHttpSession mockHttpSession = new MockHttpSession();
        mockHttpSession.setAttribute(SessionConst.LOGIN_MEMBER, memberDto);

        MemberLoginRequest memberLoginRequest = new MemberLoginRequest("test@test.com", "12345678");

        mockMvc.perform(post("/member/check/pwd")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", memberLoginRequest.getEmail())
                        .param("password", memberLoginRequest.getPassword())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection()) // 리다이렉션 상태 코드
                .andExpect(redirectedUrl("/member/edit/pwd")); // 리다이렉션 URL
    }

    @Test
    @DisplayName("비밀번호 체크(pwd) POST 실패(1) - 유효성 검사 실패(빈 값 입력)")
    void post_checkPwdPwd_fail() throws Exception {

        MemberDto memberDto = new MemberDto(0L, "test", "12345678", "test@test.com", 0L, Role.USER, "010-0000-0000", Grade.ALPHA);
        MockHttpSession mockHttpSession = new MockHttpSession();
        mockHttpSession.setAttribute(SessionConst.LOGIN_MEMBER, memberDto);

        Map<String, String> validatorResult = new HashMap<>();

        validatorResult.put("valid_password", "패스워드를 입력해주세요.");

        given(memberService.validateHandling(any(BindingResult.class))).willReturn(validatorResult);

        MemberLoginRequest memberLoginRequest = new MemberLoginRequest(" ", " ");

        mockMvc.perform(post("/member/check/pwd")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("password", memberLoginRequest.getPassword())
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("log/check_password_pwd"));
    }

    @Test
    @DisplayName("비밀번호 체크(pwd) POST 실패(2) - 잘못된 password")
    void post_checkPwdPwd_fail2() throws Exception {

        given(memberService.passwordCheck(any(MemberLoginRequest.class))).willReturn(true);

        MemberLoginRequest memberLoginRequest = new MemberLoginRequest("test@test.com", "12345678");

        mockMvc.perform(post("/member/check/pwd")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", memberLoginRequest.getEmail())
                        .param("password", memberLoginRequest.getPassword())
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("log/check_password_pwd"));

        verify(memberService).passwordCheck(any(MemberLoginRequest.class));
    }

    @Test
    @DisplayName("비밀번호 체크(delete) POST 성공 - 유효성 검사 통과")
    void post_checkPwdDelete() throws Exception {

        MemberDto memberDto = new MemberDto(0L, "test", "12345678", "test@test.com", 0L, Role.USER, "010-0000-0000", Grade.ALPHA);
        MockHttpSession mockHttpSession = new MockHttpSession();
        mockHttpSession.setAttribute(SessionConst.LOGIN_MEMBER, memberDto);

        MemberLoginRequest memberLoginRequest = new MemberLoginRequest("test@test.com", "12345678");

        mockMvc.perform(post("/member/check/delete")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", memberLoginRequest.getEmail())
                        .param("password", memberLoginRequest.getPassword())
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("/member/delete"));
    }

    @Test
    @DisplayName("비밀번호 체크(delete) POST 실패(1) - 유효성 검사 실패(빈 값 입력)")
    void post_checkPwdDelete_fail() throws Exception {

        MemberDto memberDto = new MemberDto(0L, "test", "12345678", "test@test.com", 0L, Role.USER, "010-0000-0000", Grade.ALPHA);
        MockHttpSession mockHttpSession = new MockHttpSession();
        mockHttpSession.setAttribute(SessionConst.LOGIN_MEMBER, memberDto);

        Map<String, String> validatorResult = new HashMap<>();

        validatorResult.put("valid_password", "패스워드를 입력해주세요.");

        given(memberService.validateHandling(any(BindingResult.class))).willReturn(validatorResult);

        MemberLoginRequest memberLoginRequest = new MemberLoginRequest(" ", " ");

        mockMvc.perform(post("/member/check/delete")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("password", memberLoginRequest.getPassword())
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("log/check_password_delete"));
    }

    @Test
    @DisplayName("비밀번호 체크(delete) POST 실패(2) - 잘못된 password")
    void post_checkPwdDelete_fail2() throws Exception {

        given(memberService.passwordCheck(any(MemberLoginRequest.class))).willReturn(true);

        MemberLoginRequest memberLoginRequest = new MemberLoginRequest("test@test.com", "12345678");

        mockMvc.perform(post("/member/check/delete")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", memberLoginRequest.getEmail())
                        .param("password", memberLoginRequest.getPassword())
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("log/check_password_delete"));

        verify(memberService).passwordCheck(any(MemberLoginRequest.class));
    }

    /**
     * 회원 정보 수정 & 비밀번호 변경
     */
    @Test
    @DisplayName("회원 정보 수정 GET 성공(1) - info & Role이 GUEST가 아닐 때")
    void get_editPwd_success() throws Exception {
        MemberDto memberDto = new MemberDto(0L, "test", "12345678", "test@test.com", 0L, Role.USER, "010-0000-0000", Grade.ALPHA);
        MockHttpSession mockHttpSession = new MockHttpSession();
        mockHttpSession.setAttribute(SessionConst.LOGIN_MEMBER, memberDto);

        mockMvc.perform(get("/member/edit/{url}", "info")
                        .session(mockHttpSession))
                .andExpect(status().isOk())
                .andExpect(view().name("log/edit_info"))
                .andExpect(model().attributeExists("memberEditInfoRequest"))
                .andExpect(model().attributeExists("original"));
    }

    @Test
    @DisplayName("비밀번호 변경 GET 성공(2) - pwd & Role이 GUEST가 아닐 때")
    void get_editPwd_success2() throws Exception {
        MemberDto memberDto = new MemberDto(0L, "test", "12345678", "test@test.com", 0L, Role.USER, "010-0000-0000", Grade.ALPHA);
        MockHttpSession mockHttpSession = new MockHttpSession();
        mockHttpSession.setAttribute(SessionConst.LOGIN_MEMBER, memberDto);

        mockMvc.perform(get("/member/edit/{url}", "pwd")
                        .session(mockHttpSession))
                .andExpect(status().isOk())
                .andExpect(view().name("log/edit_password"))
                .andExpect(model().attributeExists("memberEditPasswordRequest"))
                .andExpect(model().attributeExists("original"));
    }

    @Test
    @DisplayName("회원 정보 수정 GET 성공(3) - oauth & Role이 GUEST일 때")
    void get_editPwd_success3() throws Exception {
        MemberDto memberDto = new MemberDto(0L, "test", "12345678", "test@test.com", 0L, Role.GUEST, "010-0000-0000", Grade.ALPHA);
        MockHttpSession mockHttpSession = new MockHttpSession();
        mockHttpSession.setAttribute(SessionConst.LOGIN_MEMBER, memberDto);

        mockMvc.perform(get("/member/edit/{url}", "oauth")
                        .session(mockHttpSession))
                .andExpect(status().isOk())
                .andExpect(view().name("log/edit_oauth"))
                .andExpect(model().attributeExists("memberOAuthRequest"))
                .andExpect(model().attributeExists("original"));
    }

    @Test
    @DisplayName("회원 정보 수정 & 비밀번호 변경 GET 실패(1) - 잘못된 url 요청")
    void get_editPwd_fail() throws Exception {
        MemberDto memberDto = new MemberDto(0L, "test", "12345678", "test@test.com", 0L, Role.USER, "010-0000-0000", Grade.ALPHA);
        MockHttpSession mockHttpSession = new MockHttpSession();
        mockHttpSession.setAttribute(SessionConst.LOGIN_MEMBER, memberDto);

        mockMvc.perform(get("/member/edit/{url}", "error")
                        .session(mockHttpSession))
                .andExpect(status().isOk())
                .andExpect(view().name("error/404"));
    }

    @Test
    @DisplayName("회원 정보 수정 & 비밀번호 변경 GET 실패(2) - 세션 X")
    void get_editPwd_fail2() throws Exception {
        MockHttpSession mockHttpSession = new MockHttpSession();

        mockMvc.perform(get("/member/edit/{url}", "info")
                        .session(mockHttpSession))
                .andExpect(status().is3xxRedirection()) // 리다이렉션 상태 코드
                .andExpect(redirectedUrl("/member/login")); // 리다이렉션 URL
    }

    @Test
    @DisplayName("회원 정보 수정 & 비밀번호 변경 GET 실패(3) - info & Role이 GUEST 일 때")
    void get_editPwd_fail3() throws Exception {
        MemberDto memberDto = new MemberDto(0L, "test", "12345678", "test@test.com", 0L, Role.GUEST, "010-0000-0000", Grade.ALPHA);
        MockHttpSession mockHttpSession = new MockHttpSession();
        mockHttpSession.setAttribute(SessionConst.LOGIN_MEMBER, memberDto);

        mockMvc.perform(get("/member/edit/{url}", "info")
                        .session(mockHttpSession))
                .andExpect(status().is3xxRedirection()) // 리다이렉션 상태 코드
                .andExpect(redirectedUrl("/member/edit/oauth")); // 리다이렉션 URL
    }

    @Test
    @DisplayName("비밀번호 변경 GET 실패(4) - pwd & Role이 GUEST 일 때")
    void get_editPwd_fail4() throws Exception {
        MemberDto memberDto = new MemberDto(0L, "test", "12345678", "test@test.com", 0L, Role.GUEST, "010-0000-0000", Grade.ALPHA);
        MockHttpSession mockHttpSession = new MockHttpSession();
        mockHttpSession.setAttribute(SessionConst.LOGIN_MEMBER, memberDto);

        mockMvc.perform(get("/member/edit/{url}", "pwd")
                        .session(mockHttpSession))
                .andExpect(status().is3xxRedirection()) // 리다이렉션 상태 코드
                .andExpect(redirectedUrl("/member/edit/oauth")); // 리다이렉션 URL
    }

    @Test
    @DisplayName("회원 정보 수정(oauth) POST 성공 - 유효성 검사 통과")
    void post_oauthRegister() throws Exception {

        MemberDto memberDto = new MemberDto(0L, "test", "12345678", "test@test.com", 0L, Role.USER, "010-0000-0000", Grade.ALPHA);
        MockHttpSession mockHttpSession = new MockHttpSession();
        mockHttpSession.setAttribute(SessionConst.LOGIN_MEMBER, memberDto);

        MemberOAuthRequest memberOAuthRequest = new MemberOAuthRequest("test@test.com", "test", "12345678");

        mockMvc.perform(post("/member/edit/oauth")
                        .session(mockHttpSession)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", memberOAuthRequest.getEmail())
                        .param("name", memberOAuthRequest.getName())
                        .param("password", memberOAuthRequest.getPassword())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection()) // 리다이렉션 상태 코드
                .andExpect(redirectedUrl("/member/mypage")); // 리다이렉션 URL
    }

    @Test
    @DisplayName("회원 정보 수정(oauth) POST 실패(1) - 유효성 검사 실패(빈 값 입력)")
    void post_oauthRegister_fail() throws Exception {

        Map<String, String> validatorResult = new HashMap<>();

        validatorResult.put("valid_email", "이메일을 입력해주세요.");
        validatorResult.put("valid_name", "이름을 입력해주세요.");
        validatorResult.put("valid_password", "패스워드를 입력해주세요.");

        given(memberService.validateHandling(any(BindingResult.class))).willReturn(validatorResult);

        MemberOAuthRequest memberOAuthRequest = new MemberOAuthRequest(" ", " ", " ");

        mockMvc.perform(post("/member/edit/oauth")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", memberOAuthRequest.getEmail())
                        .param("name", memberOAuthRequest.getName())
                        .param("password", memberOAuthRequest.getPassword())
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("log/edit_oauth"));
    }

    @Test
    @DisplayName("회원 정보 수정(info) POST 성공 - 유효성 검사 통과")
    void post_editInfo() throws Exception {

        MemberDto memberDto = new MemberDto(0L, "test", "12345678", "test@test.com", 0L, Role.USER, "010-0000-0000", Grade.ALPHA);
        MockHttpSession mockHttpSession = new MockHttpSession();
        mockHttpSession.setAttribute(SessionConst.LOGIN_MEMBER, memberDto);

        MemberEditInfoRequest memberEditInfoRequest = new MemberEditInfoRequest("test@test.com", "test", "010-0000-0000");

        mockMvc.perform(post("/member/edit/info")
                        .session(mockHttpSession)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", memberEditInfoRequest.getEmail())
                        .param("name", memberEditInfoRequest.getName())
                        .param("phoneNum", memberEditInfoRequest.getPhoneNum())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection()) // 리다이렉션 상태 코드
                .andExpect(redirectedUrl("/")); // 리다이렉션 URL
    }

    @Test
    @DisplayName("회원 정보 수정(info) POST 실패(1) - 유효성 검사 실패(빈 값 입력)")
    void post_editInfo_fail() throws Exception {

        Map<String, String> validatorResult = new HashMap<>();

        validatorResult.put("valid_email", "이메일을 입력해주세요.");
        validatorResult.put("valid_name", "이름을 입력해주세요.");
        validatorResult.put("valid_phoneNum", "핸드폰 번호를 입력해주세요.");

        given(memberService.validateHandling(any(BindingResult.class))).willReturn(validatorResult);

        MemberEditInfoRequest memberEditInfoRequest = new MemberEditInfoRequest(" ", " ", " ");

        mockMvc.perform(post("/member/edit/info")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", memberEditInfoRequest.getEmail())
                        .param("name", memberEditInfoRequest.getName())
                        .param("phoneNum", memberEditInfoRequest.getPhoneNum())
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("log/edit_info"));
    }

    @Test
    @DisplayName("비밀번호 변경 POST 성공 - 유효성 검사 통과")
    void post_editPwd() throws Exception {

        MemberDto memberDto = new MemberDto(0L, "test", "12345678", "test@test.com", 0L, Role.USER, "010-0000-0000", Grade.ALPHA);
        MockHttpSession mockHttpSession = new MockHttpSession();
        mockHttpSession.setAttribute(SessionConst.LOGIN_MEMBER, memberDto);

        MemberPasswordRequest memberPasswordRequest = new MemberPasswordRequest("12345678");

        mockMvc.perform(post("/member/edit/pwd")
                        .session(mockHttpSession)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("password", memberPasswordRequest.getPassword())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection()) // 리다이렉션 상태 코드
                .andExpect(redirectedUrl("/")); // 리다이렉션 URL
    }

    @Test
    @DisplayName("비밀번호 변경 POST 실패(1) - 유효성 검사 실패(빈 값 입력)")
    void post_editPwd_fail() throws Exception {

        Map<String, String> validatorResult = new HashMap<>();

        validatorResult.put("valid_password", "패스워드를 입력해주세요.");

        given(memberService.validateHandling(any(BindingResult.class))).willReturn(validatorResult);

        MemberPasswordRequest memberPasswordRequest = new MemberPasswordRequest(" ");

        mockMvc.perform(post("/member/edit/pwd")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("password", memberPasswordRequest.getPassword())
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("log/edit_password"));
    }

    /**
     * 회원 탈퇴
     */
    @Test
    @DisplayName("회원 탈퇴 POST 성공 - 세션 O")
    void post_delete() throws Exception {

        MemberDto memberDto = new MemberDto(0L, "test", "12345678", "test@test.com", 0L, Role.USER, "010-0000-0000", Grade.ALPHA);
        MockHttpSession mockHttpSession = new MockHttpSession();
        mockHttpSession.setAttribute(SessionConst.LOGIN_MEMBER, memberDto);

        mockMvc.perform(post("/member/delete")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .session(mockHttpSession)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection()) // 리다이렉션 상태 코드
                .andExpect(redirectedUrl("/"))
                .andExpect(request().sessionAttribute(SessionConst.LOGIN_MEMBER, nullValue()));

        assertTrue(mockHttpSession.isInvalid());
    }

    @Test
    @DisplayName("회원 탈퇴 POST 실패 - 세션 X")
    void post_delete_fail() throws Exception {

        MockHttpSession mockHttpSession = new MockHttpSession();

        mockMvc.perform(post("/member/delete")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .session(mockHttpSession)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection()) // 리다이렉션 상태 코드
                .andExpect(redirectedUrl("/member/login")); // 리다이렉션 URL
    }

    /**
     * 마이 페이지
     */
    @Test
    @DisplayName("마이페이지 GET 성공")
    void get_mypage() throws Exception {
        MemberDto memberDto = new MemberDto(0L, "test", "12345678", "test@test.com", 0L, Role.GUEST, "010-0000-0000", Grade.ALPHA);
        MockHttpSession mockHttpSession = new MockHttpSession();
        mockHttpSession.setAttribute(SessionConst.LOGIN_MEMBER, memberDto);

        given(myPageService.selectTickets(any(MemberDto.class))).willReturn(List.of(new Reservation()));
        given(myPageService.getCreatedDate(any(MemberDto.class))).willReturn(LocalDate.now());
        given(myPageService.selectGrade(any(Long.class))).willReturn("ALPHA");
        given(myPageService.selectMileage(any(Long.class))).willReturn(0L);

        mockMvc.perform(get("/member/mypage")
                        .session(mockHttpSession))
                .andExpect(status().isOk())
                .andExpect(view().name("mypage/mypage"))
                .andExpect(model().attributeExists("tickets"))
                .andExpect(model().attributeExists("member"))
                .andExpect(model().attributeExists("grade"))
                .andExpect(model().attributeExists("mileage"))
                .andExpect(model().attributeExists("memberDto"));
    }

    @Test
    @DisplayName("마이페이지 GET 실패(1) - 세션 X")
    void get_mypage_fail() throws Exception {
        MockHttpSession mockHttpSession = new MockHttpSession();

        mockMvc.perform(get("/member/mypage")
                        .session(mockHttpSession))
                .andExpect(status().is3xxRedirection()) // 리다이렉션 상태 코드
                .andExpect(redirectedUrl("/member/login")); // 리다이렉션 URL
    }

    /**
     * 관리자 페이지
     */
    @Test
    @DisplayName("관리자 페이지 GET 성공")
    void get_admin() throws Exception {
        MemberDto memberDto = new MemberDto(0L, "test", "12345678", "test@test.com", 0L, Role.ADMIN, "010-0000-0000", Grade.ALPHA);
        MockHttpSession mockHttpSession = new MockHttpSession();
        mockHttpSession.setAttribute(SessionConst.LOGIN_MEMBER, memberDto);

        mockMvc.perform(get("/member/admin")
                        .session(mockHttpSession))
                .andExpect(status().isOk())
                .andExpect(view().name("mypage/admin"));
    }

    @Test
    @DisplayName("관리자 페이지 GET 실패(1) - 세션 X")
    void get_admin_fail1() throws Exception {
        MockHttpSession mockHttpSession = new MockHttpSession();

        mockMvc.perform(get("/member/admin")
                        .session(mockHttpSession))
                .andExpect(status().is3xxRedirection()) // 리다이렉션 상태 코드
                .andExpect(redirectedUrl("/member/login")); // 리다이렉션 URL
    }

    @Test
    @DisplayName("관리자 페이지 GET 실패(2) - ADMIN이 아닐때")
    void get_admin_fail2() throws Exception {
        MemberDto memberDto = new MemberDto(0L, "test", "12345678", "test@test.com", 0L, Role.USER, "010-0000-0000", Grade.ALPHA);
        MockHttpSession mockHttpSession = new MockHttpSession();
        mockHttpSession.setAttribute(SessionConst.LOGIN_MEMBER, memberDto);

        mockMvc.perform(get("/member/admin")
                        .session(mockHttpSession))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/member/login"));
    }
}