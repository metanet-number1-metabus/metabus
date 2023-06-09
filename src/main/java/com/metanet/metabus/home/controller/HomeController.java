package com.metanet.metabus.home.controller;

import com.metanet.metabus.member.controller.SessionConst;
import com.metanet.metabus.member.dto.MemberDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

@Controller
@RequestMapping("")
public class HomeController {
    @GetMapping("")
    public String index(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) MemberDto memberDto, Model model) {
        model.addAttribute("memberDto", memberDto);
        return "main/index";
    }
}
