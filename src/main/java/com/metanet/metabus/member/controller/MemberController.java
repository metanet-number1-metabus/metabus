package com.metanet.metabus.member.controller;

import com.metanet.metabus.bus.entity.Reservation;
import com.metanet.metabus.member.dto.*;
import com.metanet.metabus.member.entity.Role;
import com.metanet.metabus.member.service.MemberService;
import com.metanet.metabus.member.service.MyPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final MyPageService mypageService;

    /**
     * 회원 가입
     */
    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("memberRegisterRequest", new MemberRegisterRequest());
        return "log/register";
    }

    @PostMapping("/register")
    public String register(@Valid MemberRegisterRequest memberRegisterRequest, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("memberRegisterRequest", memberRegisterRequest);

            Map<String, String> validatorResult = memberService.validateHandling(bindingResult);

            for (String key : validatorResult.keySet()) {
                model.addAttribute(key, validatorResult.get(key));
            }
            return "log/register";
        } else {
            memberService.register(memberRegisterRequest);
            return "redirect:/";
        }
    }


    /**
     * 로그인
     */
    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("memberLoginRequest", new MemberLoginRequest());
        return "log/login";
    }

    @PostMapping("/login")
    public String login(@Valid MemberLoginRequest memberLoginRequest, BindingResult bindingResult, HttpServletRequest httpServletRequest, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("memberLoginRequest", memberLoginRequest);

            Map<String, String> validatorResult = memberService.validateHandling(bindingResult);

            for (String key : validatorResult.keySet()) {
                model.addAttribute(key, validatorResult.get(key));
            }
            return "log/login";
        } else {
            MemberDto loginMember = memberService.login(memberLoginRequest);

            HttpSession httpSession = httpServletRequest.getSession();
            httpSession.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);

            return "redirect:/";
        }
    }

    /**
     * 로그아웃
     */
    @PostMapping("/logout")
    public String logout(HttpServletRequest httpServletRequest) {
        HttpSession httpSession = httpServletRequest.getSession(false);
        if (httpSession.getAttribute(SessionConst.LOGIN_MEMBER) != null) {
            httpSession.invalidate();
        }
        return "redirect:/";
    }

    /**
     * 비밀번호 체크
     */
    @GetMapping("/check/{url}")
    public String checkPwd(@PathVariable("url") String url, @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) MemberDto memberDto, Model model) {
        if (memberDto == null) {
            return "redirect:/member/login";
        }

        if (memberDto.getRole().equals(Role.GUEST)) {
            return "redirect:/member/edit/oauth";
        }

        if (url.equals("info")) {
            model.addAttribute("memberLoginRequest", new MemberLoginRequest());
            model.addAttribute("original", memberDto);

            return "log/check_password_info";

        } else if (url.equals("pwd")) {
            model.addAttribute("memberLoginRequest", new MemberLoginRequest());
            model.addAttribute("original", memberDto);

            return "log/check_password_pwd";

        } else if(url.equals("delete")){
            model.addAttribute("memberLoginRequest", new MemberLoginRequest());
            model.addAttribute("original", memberDto);

            return "log/check_password_delete";
        }else {
            return "/error/404";
        }

    }

    @PostMapping("/check/info")
    public String checkPwdInfo(@Valid MemberLoginRequest memberLoginRequest, BindingResult bindingResult, HttpServletRequest httpServletRequest, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("memberLoginRequest", memberLoginRequest);
            Map<String, String> validatorResult = memberService.validateHandling(bindingResult);

            for (String key : validatorResult.keySet()) {
                model.addAttribute(key, validatorResult.get(key));
            }
            return "log/check_password_info";
        } else {
            HttpSession httpSession = httpServletRequest.getSession(false);

            MemberDto memberDto = (MemberDto) httpSession.getAttribute(SessionConst.LOGIN_MEMBER);
            memberService.checkPwd(memberLoginRequest, memberDto);

            return "redirect:/member/edit/info";
        }
    }

    @PostMapping("/check/pwd")
    public String checkPwdPwd(@Valid MemberLoginRequest memberLoginRequest, BindingResult bindingResult, HttpServletRequest httpServletRequest, Model model) {
        if (bindingResult.hasErrors()) {

            model.addAttribute("memberEditPasswordRequest", memberLoginRequest);

            Map<String, String> validatorResult = memberService.validateHandling(bindingResult);

            for (String key : validatorResult.keySet()) {
                model.addAttribute(key, validatorResult.get(key));
            }
            return "log/check_password_delete";
        } else {
            HttpSession httpSession = httpServletRequest.getSession(false);
            MemberDto memberDto = (MemberDto) httpSession.getAttribute(SessionConst.LOGIN_MEMBER);

            memberService.checkPwd(memberLoginRequest, memberDto);

            return "redirect:/member/edit/pwd";
        }
    }

    @PostMapping("/check/delete")
    public String checkPwdDelete(@Valid MemberLoginRequest memberLoginRequest, BindingResult bindingResult, HttpServletRequest httpServletRequest, Model model) {
        if (bindingResult.hasErrors()) {

            model.addAttribute("memberEditPasswordRequest", memberLoginRequest);

            Map<String, String> validatorResult = memberService.validateHandling(bindingResult);

            for (String key : validatorResult.keySet()) {
                model.addAttribute(key, validatorResult.get(key));
            }
            return "log/check_password_delete";
        } else {
            HttpSession httpSession = httpServletRequest.getSession(false);
            MemberDto memberDto = (MemberDto) httpSession.getAttribute(SessionConst.LOGIN_MEMBER);

            memberService.checkPwd(memberLoginRequest, memberDto);

            return "forward:/member/delete";
        }
    }

    /**
     * 회원 정보 수정 & 비밀번호 변경
     */
    @GetMapping("/edit/{url}")
    public String editPwd(@PathVariable("url") String url, @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) MemberDto memberDto, Model model) {
        if (memberDto == null) {
            return "redirect:/member/login";
        }

        if (url.equals("info")) {
            if (memberDto.getRole().equals(Role.GUEST)) {
                return "redirect:/member/edit/oauth";
            }
            model.addAttribute("memberEditInfoRequest", new MemberEditInfoRequest());
            model.addAttribute("original", memberDto);

            return "log/edit_info";

        } else if (url.equals("pwd")) {
            if (memberDto.getRole().equals(Role.GUEST)) {
                return "redirect:/member/edit/oauth";
            }
            model.addAttribute("memberEditPasswordRequest", new MemberPasswordRequest());
            model.addAttribute("original", memberDto);

            return "log/edit_password";

        } else if (url.equals("oauth")) {
            model.addAttribute("memberOAuthRequest", new MemberOAuthRequest());
            model.addAttribute("original", memberDto);

            return "log/edit_oauth";

        } else {
            return "error/404";
        }


    }

    @PostMapping("/edit/oauth")
    public String oauthRegister(@Valid MemberOAuthRequest memberOAuthRequest, BindingResult bindingResult, HttpServletRequest httpServletRequest, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("memberOAuthRequest", memberOAuthRequest);

            Map<String, String> validatorResult = memberService.validateHandling(bindingResult);

            for (String key : validatorResult.keySet()) {
                model.addAttribute(key, validatorResult.get(key));
            }
            return "log/edit_oauth";
        } else {
            HttpSession httpSession = httpServletRequest.getSession(false);

            MemberDto memberDto = (MemberDto) httpSession.getAttribute(SessionConst.LOGIN_MEMBER);
            MemberDto loginMember = memberService.editInfoOAuth(memberOAuthRequest, memberDto);

            httpSession.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);

            return "redirect:/member/mypage";
        }
    }

    @PostMapping("/edit/info")
    public String editInfo(@Valid MemberEditInfoRequest memberEditInfoRequest, BindingResult bindingResult, HttpServletRequest httpServletRequest, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("memberEditInfoRequest", memberEditInfoRequest);
            Map<String, String> validatorResult = memberService.validateHandling(bindingResult);

            for (String key : validatorResult.keySet()) {
                model.addAttribute(key, validatorResult.get(key));
            }
            return "log/edit_info";
        } else {
            HttpSession httpSession = httpServletRequest.getSession(false);

            MemberDto memberDto = (MemberDto) httpSession.getAttribute(SessionConst.LOGIN_MEMBER);
            MemberDto loginMember = memberService.editInfo(memberEditInfoRequest, memberDto);

            httpSession.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);

            return "redirect:/";
        }
    }

    @PostMapping("/edit/pwd")
    public String editPwd(@Valid MemberPasswordRequest memberEditPasswordRequest, BindingResult bindingResult, HttpServletRequest httpServletRequest, Model model) {
        if (bindingResult.hasErrors()) {

            model.addAttribute("memberEditPasswordRequest", memberEditPasswordRequest);

            Map<String, String> validatorResult = memberService.validateHandling(bindingResult);

            for (String key : validatorResult.keySet()) {
                model.addAttribute(key, validatorResult.get(key));
            }
            return "log/edit_password";
        } else {
            HttpSession httpSession = httpServletRequest.getSession(false);
            MemberDto memberDto = (MemberDto) httpSession.getAttribute(SessionConst.LOGIN_MEMBER);

            MemberDto loginMember = memberService.editPassword(memberEditPasswordRequest, memberDto);

            httpSession.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);

            return "redirect:/";
        }
    }

    /**
     * 회원 탈퇴
     */
    @PostMapping("/delete")
    public String delete(HttpServletRequest httpServletRequest) {

        HttpSession httpSession = httpServletRequest.getSession(false);
        MemberDto memberDto = (MemberDto) httpSession.getAttribute(SessionConst.LOGIN_MEMBER);

        if (memberDto == null) {
            return "redirect:/member/login";
        }

        memberService.delete(memberDto);

        httpSession.invalidate();
        return "redirect:/";
    }

    /**
     * 마이 페이지
     */
    @GetMapping("/mypage")
    public String mypage(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) MemberDto memberDto, Model model) {

        if (memberDto == null) {
            return "redirect:/member/login";
        }
        List<Reservation> reservationList = mypageService.selectTickets(memberDto);
        LocalDate createdDate = mypageService.getCreatedDate(memberDto);
        String grade = mypageService.selectGrade(memberDto.getId());
        Long mileage = mypageService.selectMileage(memberDto.getId());

        model.addAttribute("tickets", reservationList.size());
        model.addAttribute("member", memberDto);
        model.addAttribute("dates", createdDate);
        model.addAttribute("grade", grade);
        model.addAttribute("mileage", mileage);
        model.addAttribute("memberDto", memberDto);

        return "mypage/mypage";
    }

    /**
     * 관리자 페이지
     */
    @GetMapping("/admin")
    public String admin(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) MemberDto memberDto, Model model) {
        if (memberDto == null) {
            return "redirect:/member/login";
        }

        if (!memberDto.getRole().equals(Role.ADMIN)) {
            return "redirect:/member/login";
        }

        model.addAttribute("member", memberDto);
        return "mypage/admin";
    }
}
