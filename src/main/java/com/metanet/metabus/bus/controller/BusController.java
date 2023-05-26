package com.metanet.metabus.bus.controller;

import com.metanet.metabus.bus.service.BusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class BusController {

    private final BusService busService;

    @GetMapping("/bus/timetable")
    public String searchBus() {
        return "bus/busTimeTable";
    }

}
