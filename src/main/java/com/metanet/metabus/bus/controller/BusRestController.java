package com.metanet.metabus.bus.controller;

import com.metanet.metabus.bus.dto.ReservationDto;
import com.metanet.metabus.bus.dto.ReservationInfoRequest;
import com.metanet.metabus.bus.service.ReservationService;
import com.metanet.metabus.bus.service.SeatService;
import com.metanet.metabus.member.dto.MemberDto;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class BusRestController {

    private final SeatService seatService;
    private final ReservationService reservationService;

    @GetMapping("/read/{busNum}/{departureDate}")
    public List<Long> read(@PathVariable Long busNum, @PathVariable @DateTimeFormat(pattern = "yyyyMMdd") LocalDate departureDate) {
        return seatService.read(busNum, departureDate);
    }

    @PostMapping("/bus/reservation")
    public List<Long> makeReservation(HttpSession session, @RequestBody ReservationInfoRequest reservationInfoRequest) {

        MemberDto memberDto = (MemberDto) session.getAttribute("loginMember");
        return reservationService.create(memberDto, reservationInfoRequest);
    }

    @GetMapping("/read/detail/{reservationId}")
    public ReservationDto readReservationDetail(@PathVariable Long reservationId) {
        return reservationService.readReservationDetail(reservationId);
    }

    @PostMapping("/bus/payment/{merchantUid}")
    public void completePayment(@PathVariable String merchantUid) {
        reservationService.completePayment(merchantUid);
    }

}
