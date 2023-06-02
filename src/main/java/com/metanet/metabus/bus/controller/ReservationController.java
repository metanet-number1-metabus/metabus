package com.metanet.metabus.bus.controller;

import com.metanet.metabus.bus.dto.ReservationInfoRequest;
import com.metanet.metabus.bus.service.ReservationService;
import com.metanet.metabus.member.dto.MemberDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping("/bus/reservation")
    public List<Long> makeReservation(HttpSession session, @RequestBody ReservationInfoRequest reservationInfoRequest) {

        MemberDto memberDto = (MemberDto) session.getAttribute("loginMember");
        return reservationService.create(memberDto, reservationInfoRequest);
    }
}
