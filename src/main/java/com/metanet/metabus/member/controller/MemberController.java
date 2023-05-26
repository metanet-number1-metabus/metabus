package com.metanet.metabus.member.controller;

import com.metanet.metabus.common.exception.GlobalExceptionHandler;
import com.metanet.metabus.member.dto.MemberRegisterRequest;
import com.metanet.metabus.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
@Log4j2
public class MemberController {

    private final MemberService memberService;
    private static final Logger logger = LogManager.getLogger(GlobalExceptionHandler.class);

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("memberRegisterRequest", new MemberRegisterRequest());
        return "/log/register";
    }

    @PostMapping("/register")
    public String register(@Valid MemberRegisterRequest memberRegisterRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            logger.error(bindingResult.getFieldError());
            return "/log/register";
        } else {
            memberService.register(memberRegisterRequest);
            return "redirect:/";
        }
    }
}
