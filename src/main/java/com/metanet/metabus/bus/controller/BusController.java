package com.metanet.metabus.bus.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class BusController {


    @GetMapping("/search")
    public String searchBus() {
        return "bus/busTimeTable";
    }

}
