package com.metanet.metabus.member.controller;

import com.metanet.metabus.member.dto.MemberDto;
import com.metanet.metabus.member.dto.MemberLoginRequest;
import com.metanet.metabus.member.dto.MemberRegisterRequest;
import com.metanet.metabus.member.entity.Role;
import com.metanet.metabus.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Map;

@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("memberRegisterRequest", new MemberRegisterRequest());
        return "/log/register";
    }

    @PostMapping("/register")
    public String register(@Valid MemberRegisterRequest memberRegisterRequest, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("memberRegisterRequest", memberRegisterRequest);

            Map<String, String> validatorResult = memberService.validateHandling(bindingResult);

            for (String key : validatorResult.keySet()) {
                model.addAttribute(key, validatorResult.get(key));
            }
            return "/log/register";
        } else {
            memberService.register(memberRegisterRequest);
            return "redirect:/";
        }
    }

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
            return "/log/login";
        } else {
            MemberDto loginMember = memberService.login(memberLoginRequest);

            HttpSession httpSession = httpServletRequest.getSession();
            httpSession.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);

            return "redirect:/";
            //return "redirect:" + httpServletRequest.getHeader("Referer"); //이전페이지로 redirct

        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest httpServletRequest) {
        HttpSession httpSession = httpServletRequest.getSession(false);
        if (httpSession != null) {
            httpSession.invalidate();
        }
        return "redirect:/";
    }

    //로그인이 필요한 페이지(마이페이지)
    @GetMapping("/mypage")
    public String mypage(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) MemberDto memberDto, Model model) {

        if (memberDto == null) {
            return "redirect:/member/login";
        }

        model.addAttribute("member", memberDto);
        return "/mypage/charts";
    }

    //관리자 페이지
    @GetMapping("/admin")
    public String admin(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) MemberDto memberDto, Model model) {
        if (memberDto == null) {
            return "redirect:/member/login";
        }

        if (!memberDto.getRole().equals(Role.ADMIN)) {
            return "redirect:/member/login";
        }

        model.addAttribute("member", memberDto);
        return "/mypage/admin";
    }
}
