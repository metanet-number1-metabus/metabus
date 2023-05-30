package com.metanet.metabus.bus.controller;

import com.metanet.metabus.bus.dto.ReservationInfoRequest;
import com.metanet.metabus.bus.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping("/bus/reservation")
    public ResponseEntity<List<Long>> makeReservation(@RequestBody ReservationInfoRequest reservationInfoRequest) {

        List<Long> reservationIds = reservationService.create(reservationInfoRequest);

        return new ResponseEntity<>(reservationIds, HttpStatus.OK);
    }
}
