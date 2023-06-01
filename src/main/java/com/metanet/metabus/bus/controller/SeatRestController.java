package com.metanet.metabus.bus.controller;

import com.metanet.metabus.bus.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class SeatRestController {
    private final SeatService seatService;

    @GetMapping("/read/{busNum}/{departureDate}")
    public List<Long> read(@PathVariable Long busNum, @PathVariable @DateTimeFormat(pattern = "yyyyMMdd") LocalDate departureDate) {
        return seatService.read(busNum, departureDate);
    }
}
