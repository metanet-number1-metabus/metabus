package com.metanet.metabus.bus.controller;

import com.metanet.metabus.bus.entity.Reservation;
import com.metanet.metabus.bus.service.ReservationService;
import com.metanet.metabus.member.dto.MemberDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class BusController {

    private final ReservationService reservationService;

    @GetMapping("/bus/timetable")
    public String searchBus(
            @RequestParam("departurehome") String departureHome,
            @RequestParam("destinationhome") String destinationHome,
            @RequestParam("departuredate") String departureDate,
            @RequestParam(value = "roundtrip", defaultValue = "off") String roundTrip, Model model
    ) {

        model.addAttribute("departureHome", departureHome);
        model.addAttribute("destinationHome", destinationHome);
        model.addAttribute("departureDate", departureDate);
        model.addAttribute("roundTrip", roundTrip);

        return "bus/bus-time-table";
    }

    @GetMapping("/bus/reservation")
    public String readReservation(HttpSession session, Model model) {

        MemberDto memberDto = (MemberDto) session.getAttribute("loginMember");

        List<Reservation> allReservationList = reservationService.readAllReservation(memberDto);
        List<Reservation> unpaidReservationList = reservationService.readUnpaidReservation(memberDto);
        List<Reservation> paidReservationList = reservationService.readPaidReservation(memberDto);
        List<Reservation> pastReservationList = reservationService.readPastReservation(memberDto);

        model.addAttribute("allReservationList", allReservationList);
        model.addAttribute("unpaidReservationList", unpaidReservationList);
        model.addAttribute("paidReservationList", paidReservationList);
        model.addAttribute("pastReservationList", pastReservationList);

        return "bus/bus-reservation-table";
    }

    @PostMapping("/bus/payment")
    public String getPaymentList(@RequestParam("data") Long[] reservationIds, Model model) {

        List<Reservation> reservationList = reservationService.readByReservationId(reservationIds);

        model.addAttribute("reservationList", reservationList);

        return "bus/bus-payment";
    }

}