package com.metanet.metabus.member.controller;

import com.metanet.metabus.bus.dto.BusPopularRoutesRequest;
import com.metanet.metabus.bus.dto.ReservationStatusRequest;
import com.metanet.metabus.bus.entity.ReservationStatus;
import com.metanet.metabus.member.dto.MemberDto;
import com.metanet.metabus.member.dto.MemberInfoRequest;
import com.metanet.metabus.member.entity.Role;
import com.metanet.metabus.member.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/admin")
    public String admin(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) MemberDto memberDto, Model model) {

        if (memberDto == null) {
            return "redirect:/member/login";
        }
        if (!memberDto.getRole().equals(Role.ADMIN)) {
            return "/error/401";
        }

        List<ReservationStatusRequest> reservationList = adminService.findAllReservation();
        List<BusPopularRoutesRequest> busPopularRoutesRequestList = adminService.findAllBus();
        List<MemberInfoRequest> memberInfoRequestList = adminService.findAllMember();
        List<Long> countReservationUnpaidList = adminService.countReservation(ReservationStatus.UNPAID);
        List<Long> countReservationPaidList = adminService.countReservation(ReservationStatus.PAID);


        model.addAttribute("reservationList", reservationList);
        model.addAttribute("busList", busPopularRoutesRequestList);
        model.addAttribute("memberList", memberInfoRequestList);
        model.addAttribute("countUnpaidList", countReservationUnpaidList);
        model.addAttribute("countPaidList", countReservationPaidList);
        model.addAttribute("memberDto", memberDto);

        return "mypage/admin";
    }
}
