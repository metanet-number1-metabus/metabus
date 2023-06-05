package com.metanet.metabus.bus.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class BusController {

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

        return "bus/busTimeTable";
    }

    @GetMapping("/bus/reservation")
    public String makeReservation() {
        return "bus/bus-reservation-table";
    }

    @PostMapping("/bus/payment")
    public String getPaymentList(@RequestParam("data") Long[] reservationIds, Model model) {

        return "redirect:/";
    }

}